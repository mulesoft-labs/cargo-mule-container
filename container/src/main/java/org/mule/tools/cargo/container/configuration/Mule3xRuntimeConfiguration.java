package org.mule.tools.cargo.container.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;

public class Mule3xRuntimeConfiguration extends AbstractRuntimeConfiguration {

    @Override
    public ConfigurationCapability getCapability() {
        return new AbstractStandaloneLocalConfigurationCapability() {

            @Override
            protected Map<String, Boolean> getPropertySupportMap() {
                final Map<String, Boolean> propertySupportMap = new HashMap<String, Boolean>();
                return propertySupportMap;
            }
            
        };
    }

}