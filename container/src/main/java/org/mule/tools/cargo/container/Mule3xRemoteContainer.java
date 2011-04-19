package org.mule.tools.cargo.container;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.RuntimeConfiguration;
import org.codehaus.cargo.container.spi.AbstractRemoteContainer;

public class Mule3xRemoteContainer extends AbstractRemoteContainer {

    public static final String ID = "mule3x";
    public static final String NAME = "Mule 3.x Remote";

    public Mule3xRemoteContainer(final RuntimeConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getId() {
        return Mule3xRemoteContainer.ID;
    }

    @Override
    public String getName() {
        return Mule3xRemoteContainer.NAME;
    }

    @Override
    public ContainerCapability getCapability() {
        return new MuleContainerCapability();
    }

}