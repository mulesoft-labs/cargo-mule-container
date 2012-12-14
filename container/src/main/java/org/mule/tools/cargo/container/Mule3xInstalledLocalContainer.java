package org.mule.tools.cargo.container;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.security.Permission;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;
import org.mule.MuleServer;
import org.mule.module.launcher.MuleContainer;
import org.mule.module.reboot.DefaultMuleClassPathConfig;
import org.mule.module.reboot.MuleContainerBootstrap;
import org.mule.module.reboot.MuleContainerSystemClassLoader;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;

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
    private static final String LOG_CATEGORY = "mule:installed";
    private Object container;
    private static final String MULE_CONTAINER_CLASSNAME = "org.mule.module.launcher.MuleContainer";

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

    protected final URLClassLoader createContainerSystemClassLoader() throws Exception {
        final File muleHome = MuleContainerBootstrap.lookupMuleHome();
        final File muleBase = MuleContainerBootstrap.lookupMuleBase();
        final DefaultMuleClassPathConfig config = new DefaultMuleClassPathConfig(muleHome, muleBase) {
            {
                addLibraryDirectory(muleHome, "/lib/boot");
                addLibraryDirectory(muleHome, "/lib/conf");
            }
        };
        return new MuleContainerSystemClassLoader(config);
    }

    /**
     * @return a new {@link MuleContainer}
     */
    protected MuleContainer createContainer() {
        return new MuleContainer();
    }

    protected final void ensureValidMuleHome(final String home) {
        final File homeFile = new File(getHome());
        if (!(homeFile.exists() && homeFile.isDirectory())) {
            throw new IllegalArgumentException("Invalid mule home <"+home+">");
        }
    }

    @Override
    protected void doStart(final JvmLauncher launcher) throws Exception {
        final String home = getHome();
        ensureValidMuleHome(home);

        // TODO: Fork JVM for container
        // TODO: Use JvmLauncher to invoke directly mule.bat or mule.sh depending on OS
        final String properties = getConfiguration().getPropertyValue("cargo.system-properties");
        if (properties != null) {
          for (final String property : properties.split(";")) {
              final String[] propertyArray = property.split("=");
              final String key = propertyArray[0].trim();
              final String value = propertyArray[1].trim();
              System.setProperty(key, value);
              getLogger().info("Set property <"+key+"> to value <"+value+">", Mule3xInstalledLocalContainer.LOG_CATEGORY);
          }
        }
        getLogger().info("Using mule installation <"+home+">", Mule3xInstalledLocalContainer.LOG_CATEGORY);

        System.setProperty(Mule3xInstalledLocalContainer.MULE_HOME, home);
        System.setProperty(Mule3xInstalledLocalContainer.MULE_BASE, home);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URLClassLoader muleClassLoader = createContainerSystemClassLoader();
        Thread.currentThread().setContextClassLoader(muleClassLoader);
        try {
            final Class<?> muleClass = Thread.currentThread().getContextClassLoader().loadClass(Mule3xInstalledLocalContainer.MULE_CONTAINER_CLASSNAME);
            final Constructor<?> c = muleClass.getConstructor(String[].class);
            final Object[] objs = {new String()};
            final Object[] passed = {objs};
            this.container = c.newInstance(new Object[] {new String[0]});
            final Method startMethod = muleClass.getMethod("start", boolean.class);
            startMethod.invoke(this.container, false);

            // TODO: Take into account poms with deployer config. This should work for modern deployables configuration.
            /* for (Deployable deployable : getConfiguration().getDeployables()) {
            	new FileDeployer(this).deploy(deployable);
						} */

        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Override
    protected void waitForCompletion(final boolean waitForStarting) throws InterruptedException {
    }

    @Override
    protected void doStop(final JvmLauncher launcher) throws Exception {
        //Ugly hack to prevent MuleContainer#shutdown to call System#exit()
        final SecurityManager securityManager = System.getSecurityManager();
        try {
            final SecurityException exception = new SecurityException();
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkPermission(final Permission permission) {
                }
                @Override
                public void checkExit(final int i) {
                    throw exception;
                }
            });
            try {
                final Method shutdownMethod = this.container.getClass().getMethod("shutdown");
                shutdownMethod.invoke(this.container);
            } catch (InvocationTargetException e) {
                //Do not propagate our own exception.
                if (e.getCause() != exception) {
                    throw e;
                }
            }
        } finally {
            System.setSecurityManager(securityManager);
        }
    }

}