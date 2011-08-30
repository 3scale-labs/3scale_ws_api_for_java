package net.threescale.api.servlet.filter;

import net.threescale.api.CommonBase;
import net.threescale.api.resteasy.AuthorizationInterceptorTest;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mortbay.jetty.servlet.Holder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public abstract class AuthorizationCommon extends CommonBase {
    protected static ServletTester tester;
    protected HttpTester request;
    protected HttpTester response;
    protected AuthorizationInterceptorTest.LocalHtpSessionAttributeListener sessionListener = new AuthorizationInterceptorTest.LocalHtpSessionAttributeListener();

    @Mock
    protected static Api2 tsServer;
    protected Holder holder;

    @Test
    public void testValidatesWithCorrectAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testPlacesResponseInSessionWhenAuthorizedOk() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
    }

    @Test
    public void testValidatesWithCorrectAppIdPassesOnToNextServletInChain() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals("Test Servlet Called", this.response.getContent());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdGives409() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(409, this.response.getStatus());
    }

    @Test
     public void testLimitsExceededWithCorrectAppIdSetsTheCorrectContextType() throws Exception {

        setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));

         assertTrue(this.response.getContentType().startsWith("text/xml"));
     }

    @Test
    public void testLimitsExceededWithCorrectAppIdGivesCorrectResponse() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(LIMITS_EXCEEDED_RESPONSE, this.response.getContent());
    }

    @Test
    public void testLimitsExceededWithCorrectAppIdClearsSessionResponse() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.containsKey("authorize_response"));
    }

    @Test
    public void testValidatesWithCorrectAppIdAndAppKey() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testValidatesWithCorrectAppIdAndAppKeyAndReferer() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", "example.org", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&app_key=3scale-3333&referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testFailsWith404OnInvalidAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(404, this.response.getStatus());
    }

    @Test
    public void testReturnsCorrectResponseOnInvalidAppId() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setURI("/?app_id=54321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(INVALID_APP_ID_RESPONSE, this.response.getContent());
    }

    @Test
    public void testResetsSessionAuthorizeResponseOnFailure() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));

        this.request.setURI("/?app_id=23454321");

        StringBuffer sb = new StringBuffer();
        sb.append(this.request.generate());
        sb.append(this.request.generate());

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(false, sessionListener.attrs.containsKey("authorize_response"));
    }

    @Test
    public void testSettingACustomAppIdParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_app_id_param_name", "my_app_id");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?my_app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomAppKeyParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_app_key_param_name", "my_app_key");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", "9876", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_app_key=9876");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomReferrerParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_referrer_param_name", "my_referrer");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", null, "example.org", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321&my_referrer=example.org");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingCustomAuthResponseAttributeName() throws Exception {

        holder.setInitParameter("ts_authorize_response_attr_name", "my_response");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.containsKey("my_response"));
    }

    @Test
    public void testSettingRedirectUrlRedirectsToCorrectURL() throws Exception {

        holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
        this.request.setURI("/?app_id=23454321");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals("http://example.org/api_error.jsp", this.response.getHeader("Location"));
    }

    @Test
     public void testSettingRedirectUrlSetsTheCorrectStatus() throws Exception {

         holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(302, this.response.getStatus());
     }

    @Test
     public void testSettingRedirectUrlPlacesAuthResponseInSession() throws Exception {

         holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
     }

    @Test
     public void testSettingRedirectUrlPlacesCorrectTypeOfAuthResponseInSession() throws Exception {

         holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(LIMITS_EXCEEDED_RESPONSE));
         this.request.setURI("/?app_id=23454321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(AuthorizeResponse.class, sessionListener.attrs.get("authorize_response").getClass());
     }

    @Test
     public void testSettingRedirectUrlPlacesErrorResponseInSession() throws Exception {

         holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
         this.request.setURI("/?app_id=54321");

         this.response.parse(tester.getResponses(request.generate()));
         assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
     }

    @Test
     public void testSettingRedirectUrlPlacesErrorResponseTypeInSession() throws Exception {

         holder.setInitParameter("ts_redirect_url", "http://example.org/api_error.jsp");
         tester.getContext().getSessionHandler().addEventListener(sessionListener);
         setProviderKey(PROVIDER_KEY);

         tester.start();

         when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
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

    protected abstract void setProviderKey(String providerKey);
    protected abstract void setInitParam(String name, String value);


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
