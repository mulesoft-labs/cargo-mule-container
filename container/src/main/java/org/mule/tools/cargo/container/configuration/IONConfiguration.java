package org.mule.tools.cargo.container.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;

/**
 * Encapsulates iON specific configuration details.
 */
public class IONConfiguration extends AbstractRuntimeConfiguration {

    private static final String DOMAIN_PROPERTY = "domain";
    private static final String USERNAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String WORKERS_PROPERTY = "workers";
    private static final int WORKERS_DEFAULT = 1;

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

    public final String getDomain() {
        return getPropertyValue(IONConfiguration.DOMAIN_PROPERTY);
    }

    public final String getUserName() {
        return getPropertyValue(IONConfiguration.USERNAME_PROPERTY);
    }

    public final String getPassword() {
        return getPropertyValue(IONConfiguration.PASSWORD_PROPERTY);
    }

    public final int getWorkers() {
        final String workersProperty = getPropertyValue(IONConfiguration.WORKERS_PROPERTY);
        if (workersProperty == null) {
            return IONConfiguration.WORKERS_DEFAULT;
        }
        final int workers = Integer.valueOf(workersProperty);
        if (workers < 0 || workers > 2) {
            throw new IllegalArgumentException("Workers must be >= 0 and <= 2");
        }
        return workers;
    }

    /*@Override
    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<String, String>(super.getProperties());
        properties.remove(IONConfiguration.DOMAIN_PROPERTY);
        properties.remove(IONConfiguration.USERNAME_PROPERTY);
        properties.remove(IONConfiguration.PASSWORD_PROPERTY);
        properties.remove(IONConfiguration.WORKERS_PROPERTY);
        return properties;
    }*/

    protected final void ensurePropertyProvided(final String property) {
        if (getPropertyValue(property) == null) {
            throw new IllegalArgumentException("Missing required "+property+" property");
        }
    }

    public final void validate() {
        ensurePropertyProvided(IONConfiguration.DOMAIN_PROPERTY);
        ensurePropertyProvided(IONConfiguration.USERNAME_PROPERTY);
        ensurePropertyProvided(IONConfiguration.PASSWORD_PROPERTY);
    }

}