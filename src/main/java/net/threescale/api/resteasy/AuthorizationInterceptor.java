package net.threescale.api.resteasy;

import net.threescale.api.servlet.filter.FilterRespondsToUser;
import net.threescale.api.servlet.filter.FilterRespondsWithRedirect;
import net.threescale.api.servlet.filter.FilterResponseSelector;
import net.threescale.api.servlet.filter.Helper;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class AuthorizationInterceptor implements PreProcessInterceptor {

    @Context
    private ServletContext context;

    @Context
    private ServletConfig config;

    private String ts_app_id = "app_id";
    private String ts_app_key = "app_key";
    private String ts_referrer = "referrer";
    private String ts_url = null;
    private String ts_provider_key = null;
    private String ts_authorize_response = "authorize_response";
    private String ts_redirect_url = null;

    private Api2 server;

    private FilterResponseSelector filterResponse;

    private static Class factoryClass = net.threescale.api.ApiFactory.class;

    @Context
    HttpServletRequest servletRequest;

    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethod resourceMethod) throws Failure, WebApplicationException {

        try {
            processInitParams();
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        }
        
        try {
            Method m = factoryClass.getMethod("createV2Api", new Class[]{String.class, String.class});
            Object factory = factoryClass.newInstance();
            server = (Api2) m.invoke(factory, ts_url, ts_provider_key);
            context.log("Create server object with url: " + ts_url + " and provider_key: " + ts_provider_key);
        }
        catch (Exception ex) {
            context.log("Could not create API object for 3scale interface", ex);
        }

        setFilterResponse();

        UriInfo uriInfo = httpRequest.getUri();
        MultivaluedMap<String, String> parameters = uriInfo.getQueryParameters();
        String api_id = parameters.getFirst(ts_app_id);
        String api_key = parameters.getFirst(ts_app_key);
        String referrer = parameters.getFirst(ts_referrer);
        HttpSession session = servletRequest.getSession();

        session.removeAttribute(ts_authorize_response);

        if (api_id != null) {
            try {
                AuthorizeResponse response = server.authorize(api_id, api_key, referrer, null);
                if (response.getAuthorized()) {
                    context.log("Authorized ok for : " + api_id);
                    session.setAttribute(ts_authorize_response, response);
                    return null;
                } else {
                    context.log("Authorize failed for: " + api_id);
                    return filterResponse.sendFailedResponse(servletRequest, 409, response);

                }
            } catch (ApiException e) {
                return filterResponse.sendFailedResponse(servletRequest, 404, e);
            }
        } else {
            context.log("api_id missing in request");
            return filterResponse.sendFailedResponse(servletRequest, 404, new ApiException(MISSING_API_ID_ERROR_XML));
        }
    }

    private static final String MISSING_API_ID_ERROR_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
            "<error code=\"api_id_not_set\">app_id was not provided in the request</error>";


    public static void setFactoryClass(Class klass) {
        factoryClass = klass;
    }

    private void setFilterResponse() {
        if (ts_redirect_url == null) {
            filterResponse = new FilterRespondsToUser();
        } else {
            filterResponse = new FilterRespondsWithRedirect(context, ts_redirect_url, ts_authorize_response);
        }
    }

    private void processInitParams() throws ServletException {

        ts_provider_key = Helper.processInitParam(config, "ts_provider_key", null);
        if (ts_provider_key == null) {
            throw new ServletException("No provider key has been set");
        }

        ts_redirect_url = Helper.processInitParam(config, "ts_redirect_url", null);
        ts_url = Helper.processInitParam(config, "ts_url", "http://su1.3scale.net");
        ts_app_id = Helper.processInitParam(config, "ts_app_id_param_name", "app_id");
        ts_app_key = Helper.processInitParam(config, "ts_app_key_param_name", "app_key");
        ts_referrer = Helper.processInitParam(config, "ts_referrer_param_name", "referrer");
        ts_authorize_response = Helper.processInitParam(config, "ts_authorize_response_attr_name", "authorize_response");
    }


}
