package net.threescale.api.servlet.filter;

import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * This class intercepts the incoming request and checks for a parameter containing the users
 * api key and (optionally) app id and / or referrer.
 * <p/>
 * If no key/id is present, or does not authorize correctly it returns an error response.
 * <p/>
 * If the key/id does authorize the authorize response is placed in the session attributes and next filter in the chain is called.
 * <p/>
 * The parameter names for the api_key, app_id, referrer and the authorization response may be overridden in the
 * configuration.
 *
 *
 * To add the filter to your filter chain you need to add some lines to your web.xml file.  This is an example.
 *
 *    <filter>
 *      <filter-name>AuthorizationFilter</filter-name>
 *      <filter-class>net.threescale.api.servlet.filter.AuthorizeServletFilter</filter-class>
 *      <init-param>
 *        <param-name>ts_provider_key</param-name>
 *        <param-value>your 3scale provider key</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>ts_app_id_param_name</param-name>
 *        <param-value>api_app_id</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>ts_app_key_param_name</param-name>
 *        <param-value>api_app_key</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>ts_referrer_param_name</param-name>
 *        <param-value>api_referrer</param-value>
 *      </init-param>
 *      <init-param>
 *        <param-name>ts_authorize_response_attr_name</param-name>
 *        <param-value>api_auth_response</param-value>
 *      </init-param>
 *    </filter>
 *
 * In this example the ts_app_id, ts_app_key, ts_referrer override the default names for the request parameters.
 * If you omit them they default to: 'app_id', 'app_key' and 'referrer'.
 * The ts_authorize_response is the attribute name used in the request's session for the Authorize response object
 * and defaults to 'authorize_response'
 *
 * Then add a mapping for the request urls:
 *
 *    <filter-mapping>
 *      <filter-name>AuthorizationFilter</filter-name>
 *      <url-pattern>/api/*</url-pattern>
 *    </filter-mapping>
 *
 * You also need to place the threescale-api.jar in 
 */
public class AuthorizeServletFilter implements Filter {

    private String ts_app_id = "app_id";
    private String ts_app_key = "app_key";
    private String ts_referrer = "referrer";
    private String ts_url = "http://su1.3scale.net";
    private String ts_provider_key = null;
    private String ts_authorize_response = "authorize_response";

    private FilterConfig filterConfig;
    private ServletContext context;
    private Api2 server;

    private static Class factoryClass = net.threescale.api.ApiFactory.class;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.context = filterConfig.getServletContext();

        String tmp = context.getInitParameter("ts_provider_key");
        if (tmp != null) {
            ts_provider_key = tmp;
        } else {
            throw new ServletException("No provider key has been set");
        }

        tmp = context.getInitParameter("ts_app_id_param_name");
        if (tmp != null) {
            ts_app_id = tmp;
        }

        tmp = context.getInitParameter("ts_app_key_param_name");
        if (tmp != null) {
            ts_app_key = tmp;
        }

        tmp = context.getInitParameter("ts_referrer_param_name");
        if (tmp != null) {
            ts_referrer = tmp;
        }

        tmp = context.getInitParameter("ts_authorize_response_attr_name");
        if (tmp != null) {
            ts_authorize_response = tmp;
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
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String api_id = httpRequest.getParameter(ts_app_id);
        String api_key = httpRequest.getParameter(ts_app_key);
        String referrer = httpRequest.getParameter(ts_referrer);
        HttpSession session = httpRequest.getSession();

        session.removeAttribute(ts_authorize_response);

        if (api_id != null) {
            try {
                AuthorizeResponse response = server.authorize(api_id, api_key, referrer);
                if (response.getAuthorized()) {
                    context.log("Authorized ok for : " + api_id);
                    session.setAttribute(ts_authorize_response, response);
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    context.log("Authorize failed for: " + api_id);
                    httpResponse.setStatus(409);
                    PrintWriter writer = httpResponse.getWriter();
                    writer.append(response.getRawMessage());
                    writer.flush();
                    session.removeAttribute(ts_authorize_response);

                }
            } catch (ApiException e) {
                httpResponse.setStatus(404);
                PrintWriter writer = httpResponse.getWriter();
                writer.append(e.getRawMessage());
                writer.flush();
            }
        } else {
            context.log("api_id missing in request");
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
