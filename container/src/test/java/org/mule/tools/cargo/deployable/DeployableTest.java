package org.mule.tools.cargo.deployable;

import org.junit.Assert;
import org.junit.Test;

public class DeployableTest  {

    @Test
    public void nameShouldNotContainExtension() {
        final String name = "name";
        final String fullZipName = name+".zip";
        Assert.assertEquals(new ZipApplicationDeployable(fullZipName).getApplicationName(), name);
        Assert.assertEquals(new ZipApplicationDeployable(fullZipName).getFile(), fullZipName);
        final String fullMuleName = name+".mule";
        Assert.assertEquals(new MuleApplicationDeployable(fullMuleName).getApplicationName(), name);
        Assert.assertEquals(new MuleApplicationDeployable(fullMuleName).getFile(), fullZipName);
    }

}