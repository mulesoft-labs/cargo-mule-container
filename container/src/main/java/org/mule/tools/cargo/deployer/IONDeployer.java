package org.mule.tools.cargo.deployer;

import org.mule.tools.cargo.deployable.MuleApplicationDeployable;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.deployer.DeployerType;
import org.codehaus.cargo.container.spi.deployer.AbstractDeployer;

public class IONDeployer extends AbstractDeployer {

    public IONDeployer(final RemoteContainer container) {
    }

    @Override
    public DeployerType getType() {
        return DeployerType.REMOTE;
    }

    @Override
    public void deploy(final Deployable deployable) {
    }

    @Override
    public void undeploy(final Deployable deployable) {
        if (deployable instanceof MuleApplicationDeployable) {
            final MuleApplicationDeployable puDeployable = (MuleApplicationDeployable) deployable;

        } else {
            throw new DeployableException("Deployable type <" + deployable.getType() + "> is not supported!");
        }
    }

}