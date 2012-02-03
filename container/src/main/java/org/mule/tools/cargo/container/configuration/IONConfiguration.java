package org.mule.tools.cargo.container.configuration;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;

/**
 * Encapsulates iON specific configuration details.
 */
public class IONConfiguration extends AbstractRuntimeConfiguration {

    private static final String ION_URL_PROPERTY = "url";
    private static final String ION_URL_DEFAULT = "https://muleion.com/";
    private static final String DOMAIN_PROPERTY = "domain";
    private static final String USERNAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String WORKERS_PROPERTY = "workers";
    private static final String WORKERS_DEFAULT = "1";
    private static final String MULE_VERSION_PROPERTY = "muleVersion";
    private static final String MULE_VERSION_DEFAULT = "3.2.0";
    private static final String MAX_WAIT_TIME_PROPERTY = "maxWaitTime";
    private static final String MAX_WAIT_TIME_DEFAULT = "12000";

    protected final String getPropertyValue(final String key, final String defaultValue) {
        final String propertyValue = getPropertyValue(IONConfiguration.ION_URL_PROPERTY);
        if (propertyValue == null) {
            return defaultValue;
        }
        return propertyValue;
    }

    @Override
    public ConfigurationCapability getCapability() {
        return new MuleConfigurationCapability();
    }

    public final String getIONURL() {
        return getPropertyValue(IONConfiguration.ION_URL_PROPERTY, IONConfiguration.ION_URL_DEFAULT);
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
        final int workers = Integer.valueOf(getPropertyValue(IONConfiguration.WORKERS_PROPERTY, IONConfiguration.WORKERS_DEFAULT));
        if (workers < 0 || workers > 2) {
            throw new IllegalArgumentException("Workers must be >= 0 and <= 2");
        }
        return workers;
    }

    public final String getMuleVersion() {
        return getPropertyValue(IONConfiguration.MULE_VERSION_PROPERTY, IONConfiguration.MULE_VERSION_DEFAULT);
    }

    public final long getMaxWaitTime() {
        return Long.valueOf(getPropertyValue(IONConfiguration.MAX_WAIT_TIME_PROPERTY, IONConfiguration.MAX_WAIT_TIME_DEFAULT));
    }

    protected final void ensurePropertyProvided(final String property) {
        if (getPropertyValue(property) == null) {
            throw new IllegalArgumentException("Missing required "+property+" property");
        }
    }

    @Override
    public final void verify() {
        ensurePropertyProvided(IONConfiguration.DOMAIN_PROPERTY);
        ensurePropertyProvided(IONConfiguration.USERNAME_PROPERTY);
        ensurePropertyProvided(IONConfiguration.PASSWORD_PROPERTY);
    }

}