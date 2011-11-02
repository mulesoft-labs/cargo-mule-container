package org.mule.tools.cargo.container.configuration;


import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;

/**
 * Encapsulates iON specific configuration details.
 */
public class IONConfiguration extends AbstractRuntimeConfiguration {

    private static final String ION_URL_PROPERTY = "url";
    private static final String DOMAIN_PROPERTY = "domain";
    private static final String USERNAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String WORKERS_PROPERTY = "workers";
    private static final int WORKERS_DEFAULT = 1;
    private static final String MULE_VERSION = "muleVersion";
    private static final String MULE_VERSION_DEFAULT = "3.2.0";

    @Override
    public ConfigurationCapability getCapability() {
        return new MuleConfigurationCapability();
    }

    public final String getIONURL() {
        return getPropertyValue(IONConfiguration.ION_URL_PROPERTY);
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

    public final String getMuleVersion() {
        final String muleVersion = getPropertyValue(IONConfiguration.MULE_VERSION);
        if (muleVersion == null) {
            return IONConfiguration.MULE_VERSION_DEFAULT;
        }
        return muleVersion;
    }

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