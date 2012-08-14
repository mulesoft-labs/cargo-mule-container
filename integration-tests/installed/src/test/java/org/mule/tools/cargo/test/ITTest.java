package org.mule.tools.cargo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Assert;
import org.junit.Test;

public class ITTest {

   @Test
   public void test() throws MalformedURLException, IOException {
       final URL url = new URL("http://localhost:8084/echo");
       final URLConnection connection = url.openConnection();
       connection.connect();
       final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
       Assert.assertNotNull(reader.readLine());
   }

}
