package org.mule.tools.cargo.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A mule configuration deployable.
 */
public class MuleConfigurationDeployable extends AbstractDeployable  {

    public MuleConfigurationDeployable(final String file) {
        super(file);
    }

    @Override
    public DeployableType getType() {
        return MuleConfigurationDeployable.getDeployableType();
    }

    public static DeployableType getDeployableType() {
        return DeployableType.toType("xml");
    }

}