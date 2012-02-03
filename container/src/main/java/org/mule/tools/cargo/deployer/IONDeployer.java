package org.mule.tools.cargo.deployer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.Base64;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.deployer.DeployerType;
import org.codehaus.cargo.container.spi.deployer.AbstractDeployer;
import org.mule.tools.cargo.container.IONContainer;
import org.mule.tools.cargo.container.configuration.IONConfiguration;
import org.mule.tools.cargo.deployable.AbstractMuleDeployable;

/**
 * Deploy {@link MuleApplicationDeployable} to a Mule iON domain using REST API (http://www.mulesoft.org/documentation/display/ION/API).
 */
public class IONDeployer extends AbstractDeployer {

    private final IONContainer container;
    private final Client client;
    private static final long DEFAULT_MAX_WAIT_TIME = 120000L;
    private static final String LOG_DEPLOY_CATEGORY = "ion:deploy";
    private static final String LOG_UNDEPLOY_CATEGORY = "ion:undeploy";

    public IONDeployer(final RemoteContainer container) {
        super(container);

        if (!(container instanceof IONContainer)) {
            throw new IllegalArgumentException("Only accept "+IONContainer.class.getSimpleName());
        }

        this.container = (IONContainer) container;
        //Ensure we have all required parameters
        final ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        this.client = Client.create(clientConfig);
    }

    @Override
    public DeployerType getType() {
        return DeployerType.REMOTE;
    }

    protected final String getIONApplicationsResource() {
        return getConfiguration().getIONURL()+"api/applications/";
    }

    protected final WebResource.Builder createBuilder(final String path) {
        final WebResource webResource = this.client.resource(getIONApplicationsResource());
        return webResource.path(path).header(HttpHeaders.AUTHORIZATION, "Basic "+ new String(Base64.encode(getConfiguration().getUserName()+":"+getConfiguration().getPassword()), Charset.forName("ASCII")));
    }

    protected final IONConfiguration getConfiguration() {
        return this.container.getConfiguration();
    }

    protected final Application getIONApplication(final String domain) {
        return createBuilder(domain).type(MediaType.APPLICATION_JSON_TYPE).get(Application.class);
    }

    protected final boolean isIONApplicationCreated(final String domain) {
        try {
            getIONApplication(domain);
            return true;
        } catch (UniformInterfaceException e) {
            return false;
        }
    }

    protected final void updateIONApplication(final String domain, final Application application) {
        final ClientResponse response = createBuilder(domain).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, application);
        final ClientResponse.Status status = response.getClientResponseStatus();
        if (!(status == ClientResponse.Status.OK || status == ClientResponse.Status.CREATED)) {
            throw new DeployableException("Failed to update <"+domain+">: "+status.getStatusCode()+"("+status.getReasonPhrase()+"): "+extractFailureReason(response));
        }
    }

    /**
     * @param domain
     * @throws DeployableException if iON application does not exist
     */
    protected final void ensureIONApplicationExists(final String domain) {
        if (!isIONApplicationCreated(domain)) {
            throw new DeployableException("iON Application <"+domain+"> does not exit on <"+getConfiguration().getIONURL()+">");
        }
    }

    protected final String extractFailureReason(final ClientResponse response) {
        if (response.getType() != null && response.getType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            return response.getEntity(Map.class).get("message").toString();
        } else {
            return response.getEntity(String.class);
        }
    }

    @Override
    public void deploy(final Deployable deployable) {
        final String domain = getConfiguration().getDomain();
        ensureIONApplicationExists(domain);

        getLogger().info("Deploying <"+deployable.getFile()+">", IONDeployer.LOG_DEPLOY_CATEGORY);

        final Application application = getIONApplication(domain);
        switch (application.getStatus()) {
            case STARTED:
            case UNDEPLOYED:
            case DEPLOY_FAILED:
                //Update MetaData
                final int workers = getConfiguration().getWorkers();
                if (application.getWorkers() == workers) {
                    getLogger().info("Forcing redeployment", IONDeployer.LOG_DEPLOY_CATEGORY);
                } else {
                    getLogger().info("Scaling workers to <"+workers+">", IONDeployer.LOG_DEPLOY_CATEGORY);

                    application.setWorkers(workers);
                }

                if (!getConfiguration().getMuleVersion().equals(application.getMuleVersion())) {
                    application.setMuleVersion(getConfiguration().getMuleVersion());
                }
                application.setFilename(AbstractMuleDeployable.class.cast(deployable).getApplicationName()+".zip");

                updateIONApplication(domain, application);

                //Push new app
                final ClientResponse.Status status = createBuilder(domain+"/deploy").type(MediaType.APPLICATION_OCTET_STREAM_TYPE).post(ClientResponse.class, new File(deployable.getFile())).getClientResponseStatus();
                if (status != ClientResponse.Status.OK) {
                    throw new DeployableException("Failed to deploy <"+domain+">: "+status.getStatusCode()+"("+status.getReasonPhrase()+")");
                }
                break;
            case DEPLOYING:
                throw new DeployableException("Another deployment is in progress");
            default:
                throw new DeployableException("Unhandled status <"+application.getStatus()+">");
        }

        getLogger().info("Waiting for deployment", IONDeployer.LOG_DEPLOY_CATEGORY);

        final long maxWaitTime = getConfiguration().getMaxWaitTime();
        final long before = System.currentTimeMillis();
        while (System.currentTimeMillis() - before < maxWaitTime) {
            switch (getIONApplication(domain).getStatus()) {
                case DEPLOY_FAILED:
                    throw new DeployableException("Failed to deploy <"+deployable.getFile()+"> on <"+getConfiguration().getDomain()+">");
                case STARTED:
                    return;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //Quit loop when interrupted
                break;
            }
        }
        throw new DeployableException("Waited on <"+getConfiguration().getDomain()+"> deployment for <"+maxWaitTime+"> ms");
    }

    @Override
    public void undeploy(final Deployable deployable) {
        final String domain = getConfiguration().getDomain();
        ensureIONApplicationExists(domain);

        final Application application = getIONApplication(domain);
        application.setWorkers(0);

        getLogger().info("Updating <"+domain+">", IONDeployer.LOG_UNDEPLOY_CATEGORY);

        updateIONApplication(domain, application);

        getLogger().info("Waiting for undeployment", IONDeployer.LOG_UNDEPLOY_CATEGORY);

        final long maxWaitTime = getConfiguration().getMaxWaitTime();
        final long before = System.currentTimeMillis();
        while (System.currentTimeMillis() - before < maxWaitTime) {
            if (getIONApplication(domain).getWorkerStatuses().isEmpty()) {
                getLogger().info("Undeployed in <"+(System.currentTimeMillis()-before)+"> ms", IONDeployer.LOG_UNDEPLOY_CATEGORY);

                return;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //Quit loop when interrupted
                break;
            }
        }
        throw new DeployableException("Waited on <"+getConfiguration().getDomain()+"> undeployment for <"+maxWaitTime+"> ms");
    }

}