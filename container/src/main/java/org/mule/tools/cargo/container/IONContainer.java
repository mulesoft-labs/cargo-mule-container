package org.mule.tools.cargo.container;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.RuntimeConfiguration;
import org.codehaus.cargo.container.spi.AbstractRemoteContainer;
import org.mule.tools.cargo.container.configuration.IONConfiguration;

public class IONContainer extends AbstractRemoteContainer {

    public static final String ID = "ion";
    public static final String NAME = "Mule iON";

    public IONContainer(final RuntimeConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getId() {
        return IONContainer.ID;
    }

    @Override
    public String getName() {
        return IONContainer.NAME;
    }

    @Override
    public ContainerCapability getCapability() {
        return new MuleContainerCapability();
    }

    @Override
    public IONConfiguration getConfiguration() {
        return (IONConfiguration) super.getConfiguration();
    }

}