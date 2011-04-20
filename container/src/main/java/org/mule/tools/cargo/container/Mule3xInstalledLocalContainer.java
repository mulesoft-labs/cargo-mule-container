package org.mule.tools.cargo.container;

import java.io.File;
import java.net.URLClassLoader;
import java.security.Permission;
import org.apache.log4j.PropertyConfigurator;

import org.apache.tools.ant.taskdefs.Java;
import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.mule.module.launcher.MuleContainer;
import org.mule.module.reboot.DefaultMuleClassPathConfig;
import org.mule.module.reboot.MuleContainerBootstrap;
import org.mule.module.reboot.MuleContainerSystemClassLoader;

/**
 * Start an embedded {@link MuleServer} using maven dependencies.
 * <br />
 * Configured {@link MuleApplicationDeployable} is deployed on startup.
 */
public class Mule3xInstalledLocalContainer extends AbstractInstalledLocalContainer {

    public static final String ID = "mule3x";
    public static final String NAME = "Mule 3.x Installed";
    private static final String MULE_HOME = "mule.home";
    private static final String MULE_BASE = "mule.base";
    private MuleContainer container;

    public Mule3xInstalledLocalContainer(final LocalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getId() {
        return Mule3xInstalledLocalContainer.ID;
    }

    @Override
    public String getName() {
        return Mule3xInstalledLocalContainer.NAME;
    }

    @Override
    public ContainerCapability getCapability() {
        return new MuleContainerCapability();
    }

    protected URLClassLoader createContainerSystemClassLoader() throws Exception {
        final File muleHome = MuleContainerBootstrap.lookupMuleHome();
        final File muleBase = MuleContainerBootstrap.lookupMuleBase();
        final DefaultMuleClassPathConfig config = new DefaultMuleClassPathConfig(muleHome, muleBase);
        return new MuleContainerSystemClassLoader(config);
    }

    /**
     * @return a new {@link MuleContainer}
     */
    protected MuleContainer createContainer() {
        return new MuleContainer();
    }

    protected final synchronized  MuleContainer getContainer() {
        if (this.container == null) {
            this.container = createContainer();
        }
        return this.container;
    }

    @Override
    protected void doStart(final Java java) throws Exception {
        System.setProperty(Mule3xInstalledLocalContainer.MULE_HOME, getHome());
        System.setProperty(Mule3xInstalledLocalContainer.MULE_BASE, getHome());
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(createContainerSystemClassLoader());
        try {
            PropertyConfigurator.configure(getHome()+"/conf/log4j.properties");

            getContainer().start(false);
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Override
    protected void waitForCompletion(final boolean waitForStarting) throws InterruptedException {
    }

    @Override
    protected void doStop(final Java java) throws Exception {
        //Ugly hack to prevent MuleContainer#shutdown to call System#exit()
        final SecurityManager securityManager = System.getSecurityManager();
        try {
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkPermission(Permission prmsn) {
                }
                @Override
                public void checkExit(int i) {
                    throw new IllegalArgumentException();
                }
            });
            try {
                getContainer().shutdown();
            } catch (IllegalArgumentException e) {
            }
        } finally {
            System.setSecurityManager(securityManager);
        }
    }

}