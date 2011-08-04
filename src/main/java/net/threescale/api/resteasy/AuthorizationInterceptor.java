package net.threescale.api.resteasy;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class AuthorizationInterceptor  implements PreProcessInterceptor{
    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethod resourceMethod) throws Failure, WebApplicationException {
        ServerResponse response = new ServerResponse("", HttpServletResponse.SC_NOT_FOUND, null);
        return null;
    }
}
