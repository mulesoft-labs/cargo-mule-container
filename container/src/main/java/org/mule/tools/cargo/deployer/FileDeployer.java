package org.mule.tools.cargo.deployer;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;

import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.spi.deployer.AbstractInstalledLocalDeployer;
import org.mule.module.launcher.DeploymentService;

/**
 * Deploy {@link MuleApplicationDeployable} to a {@link Mule3xInstalledLocalContainer} by copying {@link MuleApplicationDeployable#getFile()} to {@link Mule3xInstalledLocalContainer#getHome()}/apps.
 */
public class FileDeployer extends AbstractInstalledLocalDeployer {

    private static final long SLEEP_INTERVAL = 500L;
    private long maxFileWaitTime = FileDeployer.DEFAULT_MAX_FILE_WAIT_TIME;
    private static final long DEFAULT_MAX_FILE_WAIT_TIME = 60000L;
    //Should match DeploymentService#DEFAULT_CHANGES_CHECK_INTERVAL_MS
    private static final long DEFAULT_CHANGES_CHECK_INTERVAL_MS = 5000L;
    private static final String LOG_DEPLOY_CATEGORY = "mule3x:deploy";
    private static final String LOG_UNDEPLOY_CATEGORY = "mule3x:undeploy";

    public FileDeployer(final InstalledLocalContainer container) {
        super(container);
    }

    /**
     * @param deployable
     * @throws DeployableException if provided {@link Deployable} is a {@link MuleApplicationDeployable}
     */
    protected final void ensureMuleApplication(final Deployable deployable) {
        if (!(deployable instanceof MuleApplicationDeployable)) {
            throw new DeployableException("Deployable type <" + deployable.getType() + "> is not supported!");
        }
    }

    @Override
    protected InstalledLocalContainer getContainer() {
        return (InstalledLocalContainer) super.getContainer();
    }

    /**
     * @return mule apps folder
     */
    protected final File getAppsFolder() {
        return new File(getContainer().getHome()+"/apps");
    }

    /**
     * @param deployable
     * @return anchor file for specified {@link Deployable}
     */
    protected final File getAnchorFile(final Deployable deployable) {
        return new File(getAppsFolder(), extractName(deployable)+DeploymentService.APP_ANCHOR_SUFFIX);
    }

    /**
     * @param deployable
     * @return application folder for specified {@link Deployable}
     */
    protected final File getApplicationFolder(final Deployable deployable) {
        return new File(getAppsFolder(), extractName(deployable));
    }

    /**
     * @param deployable
     * @return application name from {@link Deployable#getFile()}
     */
    protected final String extractName(final Deployable deployable) {
        final String fileName = deployable.getFile();
        return fileName.substring(fileName.lastIndexOf(File.pathSeparator)+1, fileName.length()-4);
    }

    /**
     * Wait up to timeout before anchor is discovered (using {@link File#exists()}).
     * @param file
     * @param timeout
     * @param exist if file should exist or not
     * @throws DeployableException if timeout is elapsed before anchor is discovered
     */
    protected final void waitForFile(final File file, final long timeout, final boolean exist) throws DeployableException {
        final long before = System.currentTimeMillis();
        while (System.currentTimeMillis() - before < timeout) {
            if (file.exists() == exist) {
                return;
            }

            try {
                Thread.sleep(FileDeployer.SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                //Quit loop when interrupted
                break;
            }
        }
        throw new DeployableException("Waited on <"+file+"> for <"+timeout+"> ms");
    }

    @Override
    public void deploy(final Deployable deployable) {
        ensureMuleApplication(deployable);

        final File appsFolder = getAppsFolder();
        final File sourceFile = new File(deployable.getFile());
        final File destinationFile = new File(appsFolder, sourceFile.getName());

        final String applicationName = extractName(deployable);
        getLogger().info("Deploying <"+applicationName+">", "deploy");
        getLogger().info("Copying <"+sourceFile+"> to <"+destinationFile+">", FileDeployer.LOG_DEPLOY_CATEGORY);

        try {
            FileUtils.getFileUtils().copyFile(sourceFile, destinationFile);

            final File anchorFile = getAnchorFile(deployable);

            getLogger().info("Waiting for <"+anchorFile+"> creation", FileDeployer.LOG_DEPLOY_CATEGORY);

            final long before = System.currentTimeMillis();
            waitForFile(anchorFile, this.maxFileWaitTime, true);
            getLogger().info("Deployed <"+applicationName+"> in <"+(System.currentTimeMillis()-before)+"> ms", FileDeployer.LOG_DEPLOY_CATEGORY);
        } catch (IOException e) {
            getLogger().warn("Failed to copy application to <"+appsFolder+">: "+e.toString(), FileDeployer.LOG_DEPLOY_CATEGORY);
        }
    }

    @Override
    public void undeploy(final Deployable deployable) {
        ensureMuleApplication(deployable);

        final String applicationName = extractName(deployable);

        getLogger().info("Undeploying <"+applicationName+">", FileDeployer.LOG_UNDEPLOY_CATEGORY);

        try {
            //Give AppDirWatcher a chance to notice app has been deployed.
            Thread.sleep(FileDeployer.DEFAULT_CHANGES_CHECK_INTERVAL_MS);
        } catch (InterruptedException ex) {
            //Continue
        }

        final File anchor = getAnchorFile(deployable);
        if (anchor.exists()) {
            getLogger().info("Deleting <"+anchor+">", FileDeployer.LOG_UNDEPLOY_CATEGORY);

            if (!anchor.delete()) {
                throw new DeployableException("Failed to delete <"+anchor+">");
            }

            final File applicationFolder = getApplicationFolder(deployable);

            getLogger().info("Waiting for <"+applicationFolder+"> deletion", FileDeployer.LOG_UNDEPLOY_CATEGORY);

            final long before = System.currentTimeMillis();
            waitForFile(applicationFolder, this.maxFileWaitTime, false);
            getLogger().info("Undeployed <"+applicationName+"> in <"+(System.currentTimeMillis()-before)+"> ms", FileDeployer.LOG_UNDEPLOY_CATEGORY);
        } else {
            getLogger().info("No <"+anchor+"> to delete; skipping undeployment", FileDeployer.LOG_UNDEPLOY_CATEGORY);
        }
    }

}