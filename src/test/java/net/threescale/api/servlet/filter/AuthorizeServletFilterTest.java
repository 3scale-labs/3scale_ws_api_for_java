package net.threescale.api.servlet.filter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AuthorizeServletFilterTest {

    private static ServletTester tester;
    private static String baseUrl;
    private HttpTester request;
    private HttpTester response;


  @Before
  public void setUp() throws Exception {
    this.request = new HttpTester();
    this.response = new HttpTester();
    this.request.setMethod("GET");
    this.request.setHeader("Host", "tester");
    this.request.setVersion("HTTP/1.0");
  }


    @Test
    public void testCorrectApiKey () throws Exception
    {

       this.request.setURI("/?ts_api_key=23454321");
       this.response.parse(tester.getResponses(request.generate()));
       assertTrue(this.response.getMethod() == null);
       assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testInvalidApiKey () throws Exception
    {

       this.request.setURI("/?ts_api_key=54321");
       this.response.parse(tester.getResponses(request.generate()));
       assertTrue(this.response.getMethod() == null);
       assertEquals(409, this.response.getStatus());
    }


    /**
     * This kicks off an instance of the Jetty
     * servlet container so that we can hit it.
     * We register an test service.
     */
    @BeforeClass
    public static void initServletContainer () throws Exception
    {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(AuthorizeTestServlet.class, "/");
        tester.addFilter(AuthorizeServletFilter.class, "/", 1);
        baseUrl = tester.createSocketConnector(true);
        tester.start();
    }

    /**
     * Stops the Jetty container.
     */
    @AfterClass
    public static void cleanupServletContainer () throws Exception
    {
        tester.stop();
    }
}