package net.threescale.api.servlet.filter;

import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mortbay.jetty.HttpGenerator;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorizeServletFilterTest extends AuthorizationCommon {


    /**
     * This kicks off an instance of the Jetty
     * servlet container so that we can hit it.
     * We register an test service.
     *
     * @throws Exception Throws if an error occurs in setup
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(AuthorizeTestServlet.class, "/");

        AuthorizeServletFilter.setFactoryClass(AuthorizeServletFilterTest.APITestFactory.class);

        holder = tester.addFilter(AuthorizeServletFilter.class, "/", 1);
        tester.createSocketConnector(true);

        this.request = new HttpTester();
        this.response = new HttpTester();
        this.request.setMethod("GET");
        this.request.setHeader("Host", "tester");
        this.request.setVersion("HTTP/1.0");
    }

    @Test
    public void testValidatesOkWithCorrectUserKeyThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorizeWithUserKey("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_USER_KEY_HEADER, "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testValidatesOkWithCorrectAppIdThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testPlacesResponseInSessionWhenAuthorizedOkThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(true, sessionListener.attrs.containsKey("authorize_response"));
    }


    @Test
    public void testValidatesWithCorrectAppIdAndAppKeyThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_KEY_HEADER, "3scale-3333");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testValidatesWithCorrectAppIdAndAppKeyAndRefererThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "3scale-3333", "example.org", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_KEY_HEADER, "3scale-3333");
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_REFERRER_HEADER, "example.org");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testFailsWith404OnInvalidAppIdThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "54321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(404, this.response.getStatus());
    }

    @Test
    public void testReturnsCorrectResponseOnInvalidAppIdThroughHeaders() throws Exception {

        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("54321", null, null, null)).thenThrow(new ApiException(INVALID_APP_ID_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "54321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(INVALID_APP_ID_RESPONSE, this.response.getContent());
    }

    @Test
    public void testSettingACustomAppIdHeaderParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_app_id_header_name", "my_app_id");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader("my_app_id", "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomAppKeyHeaderParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_app_key_header_name", "my_app_key");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", "9876", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setHeader("my_app_key", "9876");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomReferrerHeaderParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_referrer_header_name", "X-My-Referrer");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();


        when(tsServer.authorize("23454321", null, "example.org", null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader(AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setHeader("X-My-Referrer", "example.org");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));

        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingACustomUserKeyHeaderParmValidatesWithCorrectAppId() throws Exception {

        holder.setInitParameter("ts_user_key_header_name", "X-MyUser-Key");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorizeWithUserKey("23454321", null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader("X-MyUser-Key", "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));
        assertEquals(200, this.response.getStatus());
    }

    @Test
    public void testSettingResponseHeaderMetrics() throws Exception {

        holder.setInitParameter("ts_app_metrics_on_header", "true");
        tester.getContext().getSessionHandler().addEventListener(sessionListener);
        setProviderKey(PROVIDER_KEY);

        tester.start();

        when(tsServer.authorize("23454321", null, null, null)).thenReturn(new AuthorizeResponse(HAPPY_PATH_RESPONSE));
        this.request.setHeader( AuthorizeServletFilter.ParamsFactory.DEFAULT_TS_APP_ID_HEADER, "23454321");
        this.request.setURI("/");

        this.response.parse(tester.getResponses(request.generate()));


        String frlLimit = this.response.getHeader(AuthorizeServletFilter.RESPONSE_HEADER_USAGE_REPORT_FRL_LIMIT_KEY);
        assertEquals("20000", frlLimit);

        String frlCurrent = this.response.getHeader(AuthorizeServletFilter.RESPONSE_HEADER_USAGE_REPORT_FRL_CURRENT_KEY);
        assertEquals("17344", frlCurrent);

        String frlRemaining = this.response.getHeader(AuthorizeServletFilter.RESPONSE_HEADER_USAGE_REPORT_FRL_REMAINING_KEY);
        assertEquals("2656", frlRemaining);

        String frlPeriodStartEpoch = this.response.getHeader(AuthorizeServletFilter.RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_START_EPOCH_KEY);
        assertEquals("1280620800", frlPeriodStartEpoch);

        String frlPeriodEndEpoch = this.response.getHeader(AuthorizeServletFilter.RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_END_EPOCH_KEY);
        assertEquals("1283299200", frlPeriodEndEpoch);

    }

    /**
     * Stops the Jetty container.
     */
    @After
    public void cleanupServletContainer() throws Exception {
        tester.stop();
    }

    protected void setProviderKey(String providerKey) {
        setInitParam("ts_provider_key", providerKey);
    }

    protected void setInitParam(String name, String value) {
        holder.setInitParameter(name, value);
    }

    public static class APITestFactory extends ApiFactory {

        public static Api2 createV2Api(String url, String provider_key) {
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