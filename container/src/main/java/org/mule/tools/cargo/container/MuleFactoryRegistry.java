package org.mule.tools.cargo.container;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployer.DeployerType;
import org.codehaus.cargo.generic.AbstractFactoryRegistry;
import org.codehaus.cargo.generic.ContainerCapabilityFactory;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationCapabilityFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;
import org.codehaus.cargo.generic.packager.PackagerFactory;
import org.mule.tools.cargo.container.configuration.Mule3xLocalConfiguration;
import org.mule.tools.cargo.container.configuration.MuleConfigurationCapability;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;
import org.mule.tools.cargo.deployable.ZipApplicationDeployable;
import org.mule.tools.cargo.deployer.FileDeployer;

/**
 * Registers Mule support into default factories.
 */
public class MuleFactoryRegistry extends AbstractFactoryRegistry
{

    /**
     * Register deployable factory.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final DeployableFactory factory) {
        factory.registerDeployable(Mule3xEmbeddedLocalContainer.ID, MuleApplicationDeployable.TYPE, MuleApplicationDeployable.class);
        factory.registerDeployable(Mule3xEmbeddedLocalContainer.ID, ZipApplicationDeployable.TYPE, ZipApplicationDeployable.class);
    }

    /**
     * Register configuration capabilities.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ConfigurationCapabilityFactory factory) {
        factory.registerConfigurationCapability(Mule3xEmbeddedLocalContainer.ID, ContainerType.EMBEDDED, ConfigurationType.STANDALONE, MuleConfigurationCapability.class);
        factory.registerConfigurationCapability(Mule3xInstalledLocalContainer.ID, ContainerType.INSTALLED, ConfigurationType.STANDALONE, MuleConfigurationCapability.class);
    }

    /**
     * Register configuration factories.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ConfigurationFactory factory) {
        factory.registerConfiguration(Mule3xEmbeddedLocalContainer.ID, ContainerType.EMBEDDED, ConfigurationType.STANDALONE, Mule3xLocalConfiguration.class);
        factory.registerConfiguration(Mule3xInstalledLocalContainer.ID, ContainerType.INSTALLED, ConfigurationType.STANDALONE, Mule3xLocalConfiguration.class);
    }

    /**
     * Register deployer.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final DeployerFactory factory) {
        factory.registerDeployer(Mule3xInstalledLocalContainer.ID, DeployerType.INSTALLED, FileDeployer.class);
    }

    /**
     * Register packager. Doesn't register anything.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final PackagerFactory factory) {
    }

    /**
     * Register container.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ContainerFactory factory) {
        factory.registerContainer(Mule3xEmbeddedLocalContainer.ID, ContainerType.EMBEDDED, Mule3xEmbeddedLocalContainer.class);
        factory.registerContainer(Mule3xInstalledLocalContainer.ID, ContainerType.INSTALLED, Mule3xInstalledLocalContainer.class);
    }

    /**
     * Register container capabilities.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ContainerCapabilityFactory factory) {
        factory.registerContainerCapability(Mule3xEmbeddedLocalContainer.ID, MuleContainerCapability.class);
    }

}
