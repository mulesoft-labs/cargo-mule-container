package org.mule.tools.cargo.container;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.mule.tools.cargo.deployable.MuleApplicationDeployable;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.spi.AbstractEmbeddedLocalContainer;
import org.mule.MuleServer;
import org.mule.tools.cargo.deployable.MuleConfigurationDeployable;

/**
 * Start an embedded {@link MuleServer} using maven dependencies.
 * <br />
 * Configured {@link MuleApplicationDeployable} is deployed on startup.
 */
public class Mule3xEmbeddedLocalContainer extends AbstractEmbeddedLocalContainer {

    public static final String ID = "mule3x";
    public static final String NAME = "Mule 3.x Embedded";
    private MuleServer server;
    private static String LOG4J_PROPERTIES = "log4j.properties";

    public Mule3xEmbeddedLocalContainer(final LocalConfiguration configuration) {
        super(configuration);
    }

    @Override
    public final String getId() {
        return Mule3xEmbeddedLocalContainer.ID;
    }

    @Override
    public final String getName() {
        return Mule3xEmbeddedLocalContainer.NAME;
    }

    @Override
    public final ContainerCapability getCapability() {
        return new MuleContainerCapability() {
            @Override
            public boolean supportsDeployableType(final DeployableType type) {
                return MuleConfigurationDeployable.getDeployableType().equals(type)
                    || super.supportsDeployableType(type);
            }
        };
    }

    protected final MuleServer getServer() {
        return this.server;
    }

    protected final void startServer() {
        final MuleServer muleServer = new MuleServer();
        muleServer.start(false, false);
        this.server = muleServer;
    }

    /**
     * @return defined {@link Deployable}
     */
    protected final Deployable getDeployable() {
        final List<Deployable> deployables = getConfiguration().getDeployables();
        if (deployables.isEmpty()) {
            throw new IllegalArgumentException("No "+Deployable.class.getSimpleName()+" defined");
        }
        if (deployables.size() != 1) {
            throw new IllegalArgumentException("Only suppports a single "+Deployable.class.getSimpleName());
        }
        return deployables.get(0);
    }

    protected final void configureLog4j() {
        final String log4jProperties = getConfiguration().getPropertyValue(Mule3xEmbeddedLocalContainer.LOG4J_PROPERTIES);
        if (log4jProperties == null) {
            final Logger root = Logger.getRootLogger();
            root.setLevel(Level.INFO);
            root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
        } else {
            PropertyConfigurator.configure(log4jProperties);
        }
    }

    @Override
    protected void doStart() throws Exception {
        final Deployable deployable = getDeployable();
        if (!(deployable instanceof MuleApplicationDeployable)) {
            throw new IllegalArgumentException("Only supports "+MuleApplicationDeployable.class.getSimpleName());
        }

        configureLog4j();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final MuleApplicationDeployable muleApplicationDeployable = (MuleApplicationDeployable) deployable;
            final URLClassLoader applicationClassLoader = URLClassLoader.newInstance(new URL[]{
                new File(muleApplicationDeployable.getFile()).toURI().toURL(),
                //TODO Add support for embedded lib directory
                new URL("jar:file:"+muleApplicationDeployable.getFile()+"!/classes/")
            }, getClassLoader());
            Thread.currentThread().setContextClassLoader(applicationClassLoader);

            startServer();
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Override
    protected final void waitForCompletion(final boolean waitForStarting) throws InterruptedException {
    }

    @Override
    protected void doStop() throws Exception {
        //Don't call shutdown to prevent call to exit.
        getServer().getMuleContext().dispose();
    }

}