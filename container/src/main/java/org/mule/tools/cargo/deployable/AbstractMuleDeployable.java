package org.mule.tools.cargo.deployable;

import java.io.File;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

public abstract class AbstractMuleDeployable extends AbstractDeployable {

    public AbstractMuleDeployable(final String file) {
        super(file);
    }

    /**
     * @return name of this application extracted from file name
     */
    public final String getApplicationName() {
        final String fileName = getFile();
        return fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.lastIndexOf("."));
    }

}