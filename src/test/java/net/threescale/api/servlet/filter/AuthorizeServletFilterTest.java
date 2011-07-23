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

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.EventListener;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthorizeServletFilterTest extends CommonBase {

    private static ServletTester tester;
    private static String baseUrl;
    private HttpTester request;
    private HttpTester response;

    private LocalHtpSessionAttributeListener sessionListener = new LocalHtpSessionAttributeListener();


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

        this.request = new HttpTester();
        this.response = new HttpTester();
        this.request.setMethod("GET");
        this.request.setHeader("Host", "tester");
        this.request.setVersion("HTTP/1.0");
    }


    @Test
    public void testValidatesWithCorrectAppId() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testPlacesResponseInSessionWhenAuthorizedOk() throws Exception {

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.contains("authorize_response"));
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdGives409() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(409, this.response.getStatus());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdGivesCorrectResponse() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(LIMITS_EXCEEDED_RESPONSE, this.response.getContent());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdClearsSessionResponse() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.contains("authorize_response"));
    }


    @Test
    public void testValidatesWithCorrectAppIdAndAppKey() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testValidatesWithCorrectAppIdAndAppKeyAndReferer() throws Exception {

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", "example.org")).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333&referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testFailsWith404OnInvalidAppId() throws Exception {

        tester.start();

        when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(404, this.response.getStatus());
    }

    @Test
    public void testReturnsCorrectResponseOnInvalidAppId() throws Exception {

        tester.start();

        when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(INVALID_APP_ID_RESPONSE, this.response.getContent());
    }


    @Test
    public void testResetsSessionAuthorizeResponseOnFailure() throws Exception {

        tester.start();

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));

        this.request.setURI("/?app_id=23454321");

        StringBuffer sb = new StringBuffer();
        sb.append(this.request.generate());
        sb.append(this.request.generate());

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.contains("authorize_response"));
    }


    @Test
    public void testSettingACustomAppIdParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_app_id", "my_app_id");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();


        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?my_app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomAppKeyParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_app_key", "my_app_key");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();


        when(tsServer.authorize("23454321", "9876", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_app_key=9876");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomReferrerParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_referrer", "my_referrer");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();


        when(tsServer.authorize("23454321", null, "example.org")).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingCustomAuthResponseAttributeName() throws Exception {

        tester.getContext().getInitParams().put("ts_authorize_response", "my_response");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.contains("my_response"));
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

    public class LocalHtpSessionAttributeListener implements HttpSessionAttributeListener {

        HashSet<String> attrs = new HashSet<String>();

        @Override
        public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
            attrs.add(httpSessionBindingEvent.getName());
        }

        @Override
        public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
            attrs.remove(httpSessionBindingEvent.getName());
        }

        @Override
        public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
            if (httpSessionBindingEvent.getValue() == null) {
                attrs.remove(httpSessionBindingEvent.getName());
            }
        }
    }
}