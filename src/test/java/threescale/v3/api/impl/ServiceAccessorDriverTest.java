package threescale.v3.api.impl;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import threescale.v3.api.ServerAccessor;

/**
 * Unit Test class for the Service Api.
 */

public class ServiceAccessorDriverTest {
	private static String expectedMvenVersion="3.1.0";
	
	private ServerAccessorDriver underTest = new ServerAccessorDriver();;

		
    @Before
    public void setup() {
        
    }

    @Test
    public void testClientHeader() {
    	String actualClientHeader = underTest.getPluginHeaderValue();
    	String expectedHedaer = ServerAccessor.X_3SCALE_USER_CLIENT_HEADER_JAVA_PLUGIN+expectedMvenVersion;
    	assertEquals(actualClientHeader, expectedHedaer);
    }


}
