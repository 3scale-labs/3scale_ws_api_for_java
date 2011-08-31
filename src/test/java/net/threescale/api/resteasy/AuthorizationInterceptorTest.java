package net.threescale.api.resteasy;

import net.threescale.api.CommonBase;
import net.threescale.api.servlet.filter.AuthorizationCommon;
import net.threescale.api.servlet.filter.AuthorizeServletFilter;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorizationInterceptorTest extends AuthorizationCommon {


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
        holder = tester.addServlet(org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class, "/");
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

}