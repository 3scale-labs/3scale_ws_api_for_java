package net.threescale.api.resteasy;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.*;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

public class AuthorizationInterceptorTest {

    @Test
    public void testOne() throws URISyntaxException {


        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        POJOResourceFactory basic = new POJOResourceFactory(SimpleResource.class);
        dispatcher.getRegistry().addResourceFactory(basic);

        dispatcher.getProviderFactory().getServerPreProcessInterceptorRegistry().register(AuthorizationInterceptor.class);
    
        MockHttpRequest request = MockHttpRequest.get("/basic");
        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("basic", response.getContentAsString());
    }
}