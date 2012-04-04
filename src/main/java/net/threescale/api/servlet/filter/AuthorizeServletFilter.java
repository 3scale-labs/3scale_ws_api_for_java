package net.threescale.api.servlet.filter;

import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.AuthorizeResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 *
 * This class intercepts the incoming request and checks for a parameter containing the users
 * api key and (optionally) app id and / or referrer.
 *
 * If no key/id is present, or does not authorize correctly it returns an error response.
 *
 * If the key/id does authorize the {@link AuthorizeResponse} is placed in the session attributes
 * and the next filter in the chain is called.
 *
 * The parameter names for the <b>api_key</b>, <b>app_id</b>, <b>referrer</b> and the <b>authorization response</b> may be overridden in the
 * configuration.
 *
 *
 * To add the filter to your filter chain you need to add some lines to your web.xml file.  This is an example.
 *
 * <pre>
 * {@code
 *
 *<filter>
 *      <filter-name>AuthorizationFilter</filter-name>
 *
 *      <filter-class>net.threescale.api.servlet.filter.AuthorizeServletFilter</filter-class>
 *
 *      <init-param>
 *        <param-name>ts_provider_key</param-name>
 *        <param-value>your 3scale provider key</param-value>
 *      </init-param>
 *
 *      <init-param>
 *        <param-name>ts_redirect_url</param-name>
 *        <param-value>http://myexample.org/api_error.jsp</param-value>
 *      </init-param>
 *
 *      <init-param>
 *        <param-name>ts_app_id_param_name</param-name>
 *        <param-value>api_app_id</param-value>
 *      </init-param>
 *
 *      <init-param>
 *        <param-name>ts_app_key_param_name</param-name>
 *        <param-value>api_app_key</param-value>
 *      </init-param>
 *
 *      <init-param>
 *        <param-name>ts_referrer_param_name</param-name>
 *        <param-value>api_referrer</param-value>
 *      </init-param>
 *
 *      <init-param>
 *        <param-name>ts_authorize_response_attr_name</param-name>
 *        <param-value>api_auth_response</param-value>
 *      </init-param>
 *
 *</filter>
 * }
 * </pre>
 *
 * The {@code ts_redirect_url} is the page the request is redirected to if an authorization error occurs. If
 * this is not set the error information is returned to the user in the response body.
 * If this is set then the {@code authorization_response} session attribute will contain either an {@link AuthorizeResponse}
 * or an {@link ApiException} depending on the type of failure.  For a failed authorization due to limits exceeded etc. it
 * will be an {@link AuthorizeResponse}, anything else will set an {@link ApiException}.
 *
 * In this example the {@code ts_app_id}, {@code ts_app_key}, {@code ts_referrer} override the default names for the request parameters.
 * If you omit them they default to: 'app_id', 'app_key' and 'referrer'.
 *
 * The {@code ts_authorize_response} is the attribute name used in the request's session for the Authorize response object
 * and defaults to <i>'authorize_response'</i>.
 *
 * Then add a mapping for the request urls:
 *
 * <pre>
 * {@code
 *
 *<filter-mapping>
 *      <filter-name>AuthorizationFilter</filter-name>
 *      <url-pattern>/api/*</url-pattern>
 *</filter-mapping>
 *
 * }
 * </pre>
 *
 * You also need to place the threescale-api.jar in your classpath.
 */
public class AuthorizeServletFilter implements Filter {

    private String ts_app_id = "app_id";
    private String ts_app_key = "app_key";
    private String ts_user_key = "user_key";
    private String ts_referrer = "referrer";
    private String ts_url = "http://su1.3scale.net";
    private String ts_provider_key = null;
    private String ts_authorize_response = "authorize_response";

    private FilterConfig config;
    private ServletContext context;
    private Api2 server;

    private static Class factoryClass = net.threescale.api.ApiFactory.class;
    private FilterResponseSelector filterResponse;
    private String ts_redirect_url = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        this.context = filterConfig.getServletContext();
        
        processInitParams();
        setFilterResponse();

        try {
            Method m = factoryClass.getMethod("createV2Api", new Class[]{String.class, String.class});
            Object factory = factoryClass.newInstance();
            server = (Api2) m.invoke(factory, ts_url, ts_provider_key);
            filterConfig.getServletContext().log("Create server object with url: " + ts_url + " and provider_key: " + ts_provider_key);
        }
        catch (Exception ex) {
            filterConfig.getServletContext().log("Could not create API object for 3scale interface", ex);
        }
    }

    private void setFilterResponse() {
        if (ts_redirect_url == null) {
            filterResponse = new FilterRespondsToUser();
        } else {
            filterResponse = new FilterRespondsWithRedirect(context, ts_redirect_url, ts_authorize_response);
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String api_id = httpRequest.getParameter(ts_app_id);
        String api_key = httpRequest.getParameter(ts_app_key);
        String user_key = httpRequest.getParameter(ts_user_key);
        String referrer = httpRequest.getParameter(ts_referrer);
        HttpSession session = httpRequest.getSession();

        session.removeAttribute(ts_authorize_response);

        if (api_id != null) {
            try {
                AuthorizeResponse response = server.authorize(api_id, api_key, referrer, null);
                if (response.getAuthorized()) {
                    context.log("Authorized ok for : " + api_id);
                    session.setAttribute(ts_authorize_response, response);
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    context.log("Authorize failed for: " + api_id);
                    filterResponse.sendFailedResponse(httpRequest, httpResponse, 409, response);

                }
            } catch (ApiException e) {
                filterResponse.sendFailedResponse(httpRequest, httpResponse, 404, e);
            }
        } else if (user_key != null) {
            try {

                AuthorizeResponse response = server.authorizeWithUserKey(user_key, referrer, null);
                if (response.getAuthorized()) {
                    context.log("Authorized ok for : " + user_key);
                    session.setAttribute(ts_authorize_response, response);
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    context.log("Authorize failed for: " + api_id);
                    filterResponse.sendFailedResponse(httpRequest, httpResponse, 409, response);

                }
            } catch (ApiException e) {
            filterResponse.sendFailedResponse(httpRequest, httpResponse, 404, e);
        }
        } else {
            context.log("api_id missing in request");
            filterResponse.sendFailedResponse(httpRequest, httpResponse, 404, new ApiException(MISSING_API_ID_ERROR_XML));
        }

    }

    public static void setFactoryClass(Class klass) {
        factoryClass = klass;
    }

    private void processInitParams() throws ServletException {

        ts_provider_key = Helper.processInitParam(config, "ts_provider_key", null);
        if (ts_provider_key == null) {
            throw new ServletException("No provider key has been set for AuthorizeServeltFilter");
        }

        ts_redirect_url = Helper.processInitParam(config, "ts_redirect_url", null);
        ts_app_id = Helper.processInitParam(config, "ts_app_id_param_name", "app_id");
        ts_app_key = Helper.processInitParam(config, "ts_app_key_param_name", "app_key");
        ts_user_key = Helper.processInitParam(config, "ts_user_key_param_name", "user_key");
          ts_referrer = Helper.processInitParam(config, "ts_referrer_param_name", "referrer");
        ts_authorize_response = Helper.processInitParam(config, "ts_authorize_response_attr_name", "authorize_response");
    }


    @Override
    public void destroy() {
    }


    private static final String MISSING_API_ID_ERROR_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
            "<error code=\"api_id_not_set\">app_id was not provided in the request</error>";

}
