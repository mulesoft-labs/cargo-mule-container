package org.mule.tools.cargo.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A mule deployable. Matches http://www.mulesoft.org/documentation/display/MMP/Home packaging type.
 */
public class MuleApplicationDeployable extends AbstractDeployable  {

    public MuleApplicationDeployable(final String file) {
        super(file);
    }

    @Override
    public DeployableType getType() {
        return MuleApplicationDeployable.getDeployableType();
    }

    public static DeployableType getDeployableType() {
        return DeployableType.toType("zip");
    }

}