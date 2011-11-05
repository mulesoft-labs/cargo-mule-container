package org.mule.tools.cargo.container;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;
import org.mule.tools.cargo.deployable.ZipApplicationDeployable;

/**
 * {@link ContainerCapability} supporting {@link MuleApplicationDeployable} and {@link ZipApplicationDeployable}.
 */
public class MuleContainerCapability implements ContainerCapability {

    @Override
    public boolean supportsDeployableType(final DeployableType type) {
        return MuleApplicationDeployable.TYPE.equals(type) || ZipApplicationDeployable.TYPE.equals(type);
    }

}