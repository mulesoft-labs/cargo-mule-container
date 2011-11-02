package org.mule.tools.cargo.container.configuration;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.cargo.container.spi.configuration.AbstractConfigurationCapability;

public class MuleConfigurationCapability extends AbstractConfigurationCapability {

    @Override
    protected Map<String, Boolean> getPropertySupportMap() {
        return new HashMap<String, Boolean>();
    }

}