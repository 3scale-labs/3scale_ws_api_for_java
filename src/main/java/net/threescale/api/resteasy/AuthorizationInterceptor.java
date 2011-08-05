package net.threescale.api.resteasy;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.util.Enumeration;

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class AuthorizationInterceptor  implements PreProcessInterceptor{

   @Context ServletContext config; 

   @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethod resourceMethod) throws Failure, WebApplicationException {

       UriInfo uriInfo = httpRequest.getUri();
       String provider_key = config.getInitParameter("ts_provider_key");
       ServerResponse response = new ServerResponse("", HttpServletResponse.SC_NOT_FOUND, null);
        return null;
    }
}
