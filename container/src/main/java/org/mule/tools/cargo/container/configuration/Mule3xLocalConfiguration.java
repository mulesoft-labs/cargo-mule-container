package org.mule.tools.cargo.container.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.spi.configuration.AbstractConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractLocalConfiguration;

/**
 * Encapsulates Mule 3.x specific configuration details.
 */
public class Mule3xLocalConfiguration extends AbstractLocalConfiguration {

    public Mule3xLocalConfiguration(final String home) {
        super(home);
    }

    @Override
    protected void doConfigure(final LocalContainer container) throws Exception {
    }

    @Override
    public ConfigurationCapability getCapability() {
        return new AbstractConfigurationCapability() {
            @Override
            protected Map<String, Boolean> getPropertySupportMap() {
                return new HashMap<String, Boolean>();
            }
        };
    }

    @Override
    public ConfigurationType getType() {
        return ConfigurationType.STANDALONE;
    }

}