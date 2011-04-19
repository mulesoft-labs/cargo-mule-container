package org.mule.tools.cargo.deployer;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;

import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.spi.deployer.AbstractInstalledLocalDeployer;

/**
 * Deploy {@link MuleApplicationDeployable} to a {@link Mule3xInstalledLocalContainer} by copying {@link MuleApplicationDeployable#getFile()} to {@link Mule3xInstalledLocalContainer#getHome()}/apps.
 */
public class FileDeployer extends AbstractInstalledLocalDeployer {

    private static final String ANCHOR_SUFFIX = "-anchor.txt";

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

    protected final File getAppsFolder() {
        return new File(getContainer().getHome()+"/apps");
    }

    /**
     * @param deployable
     * @return application name from {@link Deployable#getFile()}
     */
    protected final String extractName(final Deployable deployable) {
        final String fileName = deployable.getFile();
        return fileName.substring(fileName.lastIndexOf('/')+1, fileName.length()-4);
    }

    @Override
    public void deploy(final Deployable deployable) {
        ensureMuleApplication(deployable);

        final File appsFolder = getAppsFolder();
        final File sourceFile = new File(deployable.getFile());
        final File destinationFile = new File(appsFolder, sourceFile.getName());

        //TODO Need a way to know app is fully deployed.
        getLogger().info("Deploying <"+extractName(deployable)+">", "deploy");
        getLogger().info("Copying <"+sourceFile+"> to <"+destinationFile+">", "deploy");

        try {
            FileUtils.getFileUtils().copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            getLogger().warn("Failed to copy application to <"+appsFolder+">", "deploy");
        }
    }

    @Override
    public void undeploy(final Deployable deployable) {
        ensureMuleApplication(deployable);

        getLogger().info("Undeploying "+extractName(deployable), "undeploy");

        final File appsFolder = getAppsFolder();
        final File anchor = new File(appsFolder, extractName(deployable)+FileDeployer.ANCHOR_SUFFIX);
        if (anchor.exists()) {
            getLogger().info("Deleting <"+anchor+">", "undeploy");

            anchor.delete();
        } else {
            getLogger().info("No <"+anchor+"> to delete; skipping undeployment", "undeploy");
        }
    }

}