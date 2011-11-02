package org.mule.tools.cargo.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A mule application deployable. Matches http://www.mulesoft.org/documentation/display/MMP/Home packaging type.
 */
public class MuleApplicationDeployable extends AbstractDeployable  {

    public static final DeployableType TYPE = DeployableType.toType("zip");

    public MuleApplicationDeployable(final String file) {
        super(file);
    }

    @Override
    public DeployableType getType() {
        return MuleApplicationDeployable.getDeployableType();
    }

    public static DeployableType getDeployableType() {
        return MuleApplicationDeployable.TYPE;
    }

}