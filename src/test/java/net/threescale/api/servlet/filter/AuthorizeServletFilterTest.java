package net.threescale.api.servlet.filter;

import net.threescale.api.CommonBase;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthorizeServletFilterTest extends CommonBase {

    private static ServletTester tester;
    private static String baseUrl;
    private HttpTester request;
    private HttpTester response;
    @Mock
    private static Api2 tsServer;


    /**
     * This kicks off an instance of the Jetty
     * servlet container so that we can hit it.
     * We register an test service.
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(AuthorizeTestServlet.class, "/");

        AuthorizeServletFilter.setFactoryClass(APITestFactory.class);

        tester.addFilter(AuthorizeServletFilter.class, "/", 1);
        baseUrl = tester.createSocketConnector(true);
        tester.start();

        this.request = new HttpTester();
        this.response = new HttpTester();
        this.request.setMethod("GET");
        this.request.setHeader("Host", "tester");
        this.request.setVersion("HTTP/1.0");
    }


    @Test
    public void testValidatesCorrectApiId() throws Exception {
        
        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?ts_api_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertTrue(this.response.getMethod() == null);
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testFailsWithInvalidApiId() throws Exception {

        when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?ts_api_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertTrue(this.response.getMethod() == null);
        assertEquals(404, this.response.getStatus());
        assertEquals(INVALID_APP_ID_RESPONSE, this.response.getContent());
    }



    /**
     * Stops the Jetty container.
     */
    @After
    public void cleanupServletContainer() throws Exception {
        tester.stop();
    }


    public static class APITestFactory {

        public Api2 createV2Api(String url, String provider_key) {

            return tsServer;
        }

    }
}