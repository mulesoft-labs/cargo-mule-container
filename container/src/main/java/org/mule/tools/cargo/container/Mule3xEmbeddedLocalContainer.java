package org.mule.tools.cargo.container;

import org.mule.tools.cargo.deployable.MuleApplicationDeployable;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.spi.AbstractEmbeddedLocalContainer;
import org.mule.MuleServer;

/**
 * Start an embedded {@link MuleServer} using maven dependencies.
 * <br />
 * Configured {@link MuleApplicationDeployable} is deployed on startup.
 */
public class Mule3xEmbeddedLocalContainer extends AbstractEmbeddedLocalContainer {

    public static final String ID = "mule3x";
    public static final String NAME = "Mule 3.x Embedded";
    private MuleServer server;

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
        return new MuleContainerCapability();
    }

    /**
     * @return a new {@link MuleServer}
     */
    protected MuleServer createServer() {
        return new MuleServer();
    }

    protected final synchronized  MuleServer getServer() {
        if (this.server == null) {
            this.server = createServer();
        }
        return this.server;
    }

    /**
     * @return defined {@link MuleApplicationDeployable}
     */
    protected final MuleApplicationDeployable getMuleApplication() {
        final List<Deployable> deployables = getConfiguration().getDeployables();
        if (deployables.isEmpty()) {
            throw new IllegalArgumentException("No "+MuleApplicationDeployable.class.getSimpleName()+" defined");
        }
        if (deployables.size() != 1) {
            throw new IllegalArgumentException("Only suppports a single "+MuleApplicationDeployable.class.getSimpleName());
        }
        return (MuleApplicationDeployable) deployables.get(0);
    }

    @Override
    protected void doStart() throws Exception {
        //TODO Add support for embedded lib directory
        //TODO Log4j configuration?
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final MuleApplicationDeployable muleApplication = getMuleApplication();
        final URLClassLoader applicationClassLoader = URLClassLoader.newInstance(new URL[]{
            new File(muleApplication.getFile()).toURI().toURL(),
            new URL("jar:file:"+muleApplication.getFile()+"!/classes/")
        }, getClassLoader());
        Thread.currentThread().setContextClassLoader(applicationClassLoader);
        try {
            getServer().start(false, false);
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