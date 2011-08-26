package net.threescale.api.servlet.filter;

import net.threescale.api.CommonBase;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorizeServletFilterTest extends AuthorizationCommon {


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