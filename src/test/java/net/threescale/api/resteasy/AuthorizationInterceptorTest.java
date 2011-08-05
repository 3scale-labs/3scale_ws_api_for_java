package net.threescale.api.resteasy;

import net.threescale.api.CommonBase;
import net.threescale.api.servlet.filter.AuthorizeServletFilter;
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
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorizationInterceptorTest extends CommonBase {

    private static ServletTester tester;
    private HttpTester request;
    private HttpTester response;

    private LocalHtpSessionAttributeListener sessionListener = new LocalHtpSessionAttributeListener();


    @Mock
    private static Api2 tsServer;


    /**
     * This kicks off an instance of the Jetty
     * servlet container so that we can hit it.
     * We register an test service.
     * @throws Exception  Throws if an error occurs in setup
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class, "/");
        tester.getContext().getInitParams().put("resteasy.resources", "net.threescale.api.resteasy.SimpleResource");
        tester.getContext().getInitParams().put("resteasy.providers", "net.threescale.api.resteasy.AuthorizationInterceptor");
        tester.createSocketConnector(true);

        AuthorizationInterceptor.setFactoryClass(APITestFactory.class);
        
        this.request = new HttpTester();
        this.response = new HttpTester();
        this.request.setMethod("GET");
        this.request.setHeader("Host", "tester");
        this.request.setVersion("HTTP/1.0");
    }


    @Test
    public void testValidatesWithCorrectAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testPlacesResponseInSessionWhenAuthorizedOk() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
    }

    @Test
    public void testValidatesWithCorrectAppIdPassesOnToNextServletInChain() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals("Test Servlet Called", this.response.getContent());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdGives409() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(409, this.response.getStatus());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdGivesCorrectResponse() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(LIMITS_EXCEEDED_RESPONSE, this.response.getContent());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdClearsSessionResponse() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.containsKey("authorize_response"));
    }


    @Test
    public void testValidatesWithCorrectAppIdAndAppKey() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testValidatesWithCorrectAppIdAndAppKeyAndReferer() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", "example.org")).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333&referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testFailsWith404OnInvalidAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(404, this.response.getStatus());
    }

    @Test
    public void testReturnsCorrectResponseOnInvalidAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(INVALID_APP_ID_RESPONSE, this.response.getContent());
    }


    @Test
    public void testResetsSessionAuthorizeResponseOnFailure() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));

        this.request.setURI("/?app_id=23454321");

        StringBuffer sb = new StringBuffer();
        sb.append(this.request.generate());
        sb.append(this.request.generate());

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.containsKey("authorize_response"));
    }


    @Test
    public void testSettingACustomAppIdParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_app_id_param_name", "my_app_id");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?my_app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomAppKeyParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_app_key_param_name", "my_app_key");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", "9876", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_app_key=9876");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomReferrerParmValidatesWithCorrectAppId() throws Exception {

        tester.getContext().getInitParams().put("ts_referrer_param_name", "my_referrer");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", null, "example.org")).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingCustomAuthResponseAttributeName() throws Exception {

        tester.getContext().getInitParams().put("ts_authorize_response_attr_name", "my_response");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.containsKey("my_response"));
    }


    @Test
    public void testSettingRedirectUrlRedirectsToCorrectURL() throws Exception {

        tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals("http://example.org/api_error.jsp", this.response.getHeader("Location"));
    }

    @Test
     public void testSettingRedirectUrlSetsTheCorrectStatus() throws Exception {

         tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(302, this.response.getStatus());
     }


    @Test
     public void testSettingRedirectUrlPlacesAuthResponseInSession() throws Exception {

         tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
     }

    @Test
     public void testSettingRedirectUrlPlacesCorrectTypeOfAuthResponseInSession() throws Exception {

         tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(AuthorizeResponse.class, sessionListener.attrs.get("authorize_response").getClass());
     }

    @Test
     public void testSettingRedirectUrlPlacesErrorResponseInSession() throws Exception {

         tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
         this.request.setURI("/?app_id=54321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
     }


    @Test
     public void testSettingRedirectUrlPlacesErrorResponseTypeInSession() throws Exception {

         tester.getContext().getInitParams().put("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("54321", null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
         this.request.setURI("/?app_id=54321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(ApiException.class, sessionListener.attrs.get("authorize_response").getClass());
     }


     /**
     * Stops the Jetty container.
     */
    @After
    public void cleanupServletContainer() throws Exception {
        tester.stop();
    }


    private void setProviderKey(String providerKey) {
        tester.getContext().getInitParams().put("ts_provider_key", providerKey);
     }


    public static class APITestFactory {

        public Api2 createV2Api(String url, String provider_key) {

            return tsServer;
        }

    }

    public class LocalHtpSessionAttributeListener implements HttpSessionAttributeListener {

        HashMap<String, Object> attrs = new HashMap<String, Object>();

        @Override
        public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
            attrs.put(httpSessionBindingEvent.getName(), httpSessionBindingEvent.getValue());
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