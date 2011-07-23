package net.threescale.api.servlet.filter;

import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * This class intercepts the incoming request and checks for a parameter containing either the users
 * api key (ts_api_key) or app id (ts_app_id).
 * <p/>
 * If no key/id is present, or does not authorize correctly it returns an error response or xxxx.
 * <p/>
 * If the key/id does authorize the next filter in the chain is called.
 * <p/>
 * The parameter name for the api key (ts_api_key) or app id (ts_app_id) may be overridden in the
 * configuration.
 */
public class AuthorizeServletFilter implements Filter {

    private FilterConfig filterConfig;
    private String ts_api_id = "ts_api_id";
    private String ts_api_key = "ts_api_key";
    private ServletContext context;
    private Api2 server;
    private String ts_url = "http://su1.3scale.net";
    private String ts_provider_key = null;
    private static Class factoryClass = net.threescale.api.ApiFactory.class;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.context = filterConfig.getServletContext();
        try {
            Method m = factoryClass.getMethod("createV2Api", new Class[]{String.class, String.class});
            Object factory = factoryClass.newInstance();
            server = (Api2) m.invoke(factory, ts_url, ts_provider_key);
            context.log("Create server object with url: " + ts_url + " and provider_key: " + ts_provider_key);
        }
        catch (Exception ex) {
            context.log("Could not create API object for 3scale interface", ex);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String api_id = servletRequest.getParameter(ts_api_id);
        if (api_id != null) {
            try {
                AuthorizeResponse response = server.authorize(api_id, null, null);
                if (response.getAuthorized()) {
                    context.log("Authorized ok for : " + api_id);
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    context.log("Authorize failed for: " + api_id);
                    ((HttpServletResponse) servletResponse).setStatus(409);
                }
            } catch (ApiException e) {
                httpResponse.setStatus(404);
                PrintWriter writer = httpResponse.getWriter();
                writer.append(e.getRawMessage());
                writer.flush();
            }
        } else {
            context.log("api_key missing in request");
            httpResponse.setStatus(409);
        }

    }

    public static void setFactoryClass(Class klass) {
        factoryClass = klass;
    }

    @Override
    public void destroy() {
    }
}
