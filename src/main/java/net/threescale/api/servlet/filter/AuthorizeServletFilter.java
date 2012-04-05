package net.threescale.api.servlet.filter;

import net.threescale.api.ApiFactory;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiUsageMetric;
import net.threescale.api.v2.AuthorizeResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.lang.System.out;

/**
 * This class intercepts the incoming request and checks for a parameter containing the users
 * api key and (optionally) app id and / or referrer.
 * <p/>
 * If no key/id is present, or does not authorize correctly it returns an error response.
 * <p/>
 * If the key/id does authorize the {@link AuthorizeResponse} is placed in the session attributes
 * and the next filter in the chain is called.
 * <p/>
 * The parameter names for the <b>api_key</b>, <b>app_id</b>, <b>referrer</b> and the <b>authorization response</b> may be overridden in the
 * configuration.
 * <p/>
 * <p/>
 * To add the filter to your filter chain you need to add some lines to your web.xml file.  This is an example.
 * <p/>
 * <pre>
 * {@code
 *
 * <filter>
 *      <filter-name>AuthorizationFilter</filter-name>
 *
 *      <filter-class>net.threescale.api.servlet.filter.AuthorizeServletFilter</filter-class>
 *      <!-- What you see as values parameter values are the defaults unless specified. -->
 *      <!-- Required -->
 *      <init-param>
 *        <param-name>ts_provider_key</param-name>
 *        <param-value>your 3scale provider key</param-value>
 *      </init-param>
 *      <!-- optional. If authorization fails the request will redirect to this url. Defaults to null.-->
 *      <init-param>
 *        <param-name>ts_redirect_url</param-name>
 *        <param-value>http://myexample.org/api_error.jsp</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the query string that holds the Application Id. -->
 *      <init-param>
 *        <param-name>ts_app_id_param_name</param-name>
 *        <param-value>api_app_id</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the query string that holds the Application Key. -->
 *      <init-param>
 *        <param-name>ts_app_key_param_name</param-name>
 *        <param-value>api_app_key</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the query string that holds the API Referrer. -->
 *      <init-param>
 *        <param-name>ts_referrer_param_name</param-name>
 *        <param-value>api_referrer</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the header that holds the Application Id. -->
 *      <init-param>
 *          <param-name>ts_app_id_header_name</param-name>
 *          <param-value>X-App-Id</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the header that holds the Application Key. -->
 *      <init-param>
 *          <param-name>ts_app_key_header_name</param-name>
 *          <param-value>X-App-Key</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the header that holds the User Key. -->
 *      <init-param>
 *          <param-name>ts_user_key_header_name</param-name>
 *          <param-value>X-User-Key</param-value>
 *      </init-param>
 *      <!-- optional. Name of the parameter in the header that holds the Referrer. -->
 *      <init-param>
 *          <param-name>ts_referrer_header_name</param-name>
 *          <param-value>X-Referrer</param-value>
 *      </init-param>
 *      <!-- optional. Name of the session attribute that holds the 3Scale Authentication Response. -->
 *      <init-param>
 *        <param-name>ts_authorize_response_attr_name</param-name>
 *        <param-value>api_auth_response</param-value>
 *      </init-param>
 *      <!-- optional. 3Scale's URL you should be fine with the default value. -->
 *      <init-param>
 *          <param-name>ts_url</param-name>
 *          <param-value>http://su1.3scale.net</param-value>
 *      </init-param>
 *
 * </filter>
 * }
 * </pre>
 * <p/>
 * The {@code tsRedirectUrl} is the page the request is redirected to if an authorization error occurs. If
 * this is not set the error information is returned to the user in the response body.
 * If this is set then the {@code authorization_response} session attribute will contain either an {@link AuthorizeResponse}
 * or an {@link ApiException} depending on the type of failure.  For a failed authorization due to limits exceeded etc. it
 * will be an {@link AuthorizeResponse}, anything else will set an {@link ApiException}.
 * <p/>
 * In this example the {@code ts_app_id}, {@code ts_app_key}, {@code ts_referrer} override the default names for the request parameters.
 * If you omit them they default to: 'app_id', 'app_key' and 'referrer'.
 * <p/>
 * The {@code ts_authorize_response} is the attribute name used in the request's session for the Authorize response object
 * and defaults to <i>'authorize_response'</i>.
 * <p/>
 * Then add a mapping for the request urls:
 * <p/>
 * <pre>
 * {@code
 *
 * <filter-mapping>
 *      <filter-name>AuthorizationFilter</filter-name>
 *      <url-pattern>/api/*</url-pattern>
 * </filter-mapping>
 *
 * }
 * </pre>
 * <p/>
 * You also need to place the threescale-api.jar in your classpath.
 */
public class AuthorizeServletFilter implements Filter {

    public static final String DEFAULT_TS_URL = ApiFactory.DEFAULT_3SCALE_PROVIDER_API_URL;

    public static final String DEFAULT_API_FACTORY_CLASS = "net.threescale.api.ApiFactory";

    public static final String DEFAULT_TS_AUTHORIZE_RESPONSE_ATTR_NAME = "authorize_response";

    public static final String DEFAULT_TS_APP_METRICS_ON_HEADER = "true";

    private static Class<? extends ApiFactory> factoryClass;

    static {
        try {
            AuthorizeServletFilter.factoryClass = getFactoryClass(DEFAULT_API_FACTORY_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load default Factory Class:" + DEFAULT_API_FACTORY_CLASS, e);
        }
    }

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private String tsUrl;

    private String tsProviderKey;

    private String tsAuthorizeResponse;

    private boolean tsAppMetricsOnHeader;

    private FilterConfig config;

    private ServletContext context;

    private Api2 server;

    private final Lock serverLock = new ReentrantLock();

    private FilterResponseSelector filterResponse;

    private String tsRedirectUrl;


    /**
     * @param
     */
    public static final void setFactoryClassName(String factoryClassName) throws ClassNotFoundException {
        AuthorizeServletFilter.factoryClass = AuthorizeServletFilter.getFactoryClass(factoryClassName);
    }

    /**
     * @param
     */
    public static final void setFactoryClass(Class<? extends ApiFactory> factoryClass) {
        AuthorizeServletFilter.factoryClass = factoryClass;
    }


    /**
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        this.config = filterConfig;

        this.context = filterConfig.getServletContext();

        ParamsFactory.init(filterConfig);

        processInitParams();

        initFilterResponse();

        initiateApiServer(filterConfig);
    }


    /**
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {

        out.println("doing filter...");

        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        final Params params = ParamsFactory.getFromRequest(httpRequest);

        final HttpSession session = getSession(httpRequest);

        if (params.hasAppId() || params.hasUserKey()) {
            try {
                AuthorizeResponse authResponse = params.hasAppId() ?
                        authorizeForApp(params.appId, params.appKey, params.referrer) :
                        authorizeForUser(params.userKey, params.referrer);

                String apiClientIdentifier = params.hasAppId() ? params.appId : params.userKey;

                if (authResponse.getAuthorized()) {
                    context.log(String.format("Authorized ok for [%s]", apiClientIdentifier));

                    processAuthorizationResponse(authResponse, session, httpResponse);

                    filterChain.doFilter(servletRequest, servletResponse);

                } else {
                    context.log(String.format("Authorize failed for [%s], reason:\t%s", apiClientIdentifier, authResponse.getReason()));

                    filterResponse.sendFailedResponse(httpRequest, httpResponse, 409, authResponse);

                }

            } catch (ApiException e) {
                filterResponse.sendFailedResponse(httpRequest, httpResponse, 404, e);

            }

        } else {
            context.log("api_id missing in request");

            filterResponse.sendFailedResponse(httpRequest,
                    httpResponse, 404, new ApiException(getMissingApiIdErrorMessag(httpRequest)));
        }
    }


    /**
     *
     */
    @Override
    public void destroy() {
    }


    /**
     * @throws ServletException
     */
    final void processInitParams() throws ServletException {

        tsUrl = Helper.processInitParam(config, "ts_url", DEFAULT_TS_URL);

        tsRedirectUrl = Helper.processInitParam(config, "ts_redirect_url", null);

        tsAuthorizeResponse = Helper.processInitParam(config, "ts_authorize_response_attr_name", DEFAULT_TS_AUTHORIZE_RESPONSE_ATTR_NAME);

        tsAppMetricsOnHeader = Boolean.parseBoolean(Helper.processInitParam(config, "ts_app_metrics_on_header", DEFAULT_TS_APP_METRICS_ON_HEADER));


        tsProviderKey = Helper.processInitParam(config, "ts_provider_key", null);

        if (tsProviderKey == null) {
            throw new ServletException("No provider key has been set for AuthorizeServeltFilter");
        }

    }


    /**
     * @param request
     * @return
     */
    final HttpSession getSession(HttpServletRequest request) {

        HttpSession session = request.getSession();

        session.removeAttribute(tsAuthorizeResponse);

        return session;
    }

    /**
     *
     */
    final void initFilterResponse() {
        if (tsRedirectUrl == null) {

            filterResponse = new FilterRespondsToUser();

        } else {

            filterResponse = new FilterRespondsWithRedirect(context, tsRedirectUrl, tsAuthorizeResponse);
        }
    }

    /**
     *
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings(value = "unchecked")
    protected static Class<ApiFactory> getFactoryClass(String className) throws ClassNotFoundException {
        return (Class<ApiFactory>) Class.forName(className);
    }


    /**
     *
     * @return
     */
    protected Api2 createApiServer() {
        if (factoryClass == null) {
            throw new IllegalStateException("The 3Scale API Factory Class was not initiated.");
        }

        Class<? extends ApiFactory> _factoryClass = factoryClass;

        try {
            final Object factory = _factoryClass.newInstance();

            final Method m = _factoryClass.getMethod("createV2Api", new Class[]{String.class, String.class});

            return (Api2) m.invoke(factory, tsUrl, tsProviderKey);

        } catch (Exception e) {

            throw new IllegalStateException("Unable to create 3Scale API Server", e);
        }

    }

    /**
     * @param filterConfig
     */
    final void initiateApiServer(FilterConfig filterConfig) {
        serverLock.tryLock();
        try {
            server = createApiServer();

            filterConfig.getServletContext().log(String.format("Create server object with url:[%s] and provider_key [%s]", tsUrl, tsProviderKey));

        } catch (Exception ex) {
            filterConfig.getServletContext().log("Could not create API object for 3scale interface", ex);
        } finally {
            serverLock.unlock();
        }
    }

    /**
     * @param appId
     * @param appKey
     * @param referrer
     * @return
     * @throws ApiException
     */
    protected AuthorizeResponse authorizeForApp(final String appId, final String appKey, final String referrer) throws ApiException {
        return this.getServer().authorize(appId, appKey, referrer, null);
    }

    /**
     * @param userKey
     * @param referrer
     * @return
     * @throws ApiException
     */
    protected AuthorizeResponse authorizeForUser(final String userKey, final String referrer) throws ApiException {
         return this.getServer().authorizeWithUserKey(userKey, referrer, null);
    }

    /**
     *
     * @return
     */
    protected final Api2 getServer(){
        serverLock.tryLock();
        Api2 _server = null;
        try{
            _server = this.server;
        } finally {
            serverLock.unlock();
        }
        if ( _server == null ){
            throw new IllegalStateException("The API Server hasn't been initialzied yet.");
        } else {
            return _server;
        }
    }

    /**
     * @param authResponse
     * @param session
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    final void processAuthorizationResponse(final AuthorizeResponse authResponse,
                                            final HttpSession session,
                                            final HttpServletResponse response) throws IOException, ServletException {

        session.setAttribute(tsAuthorizeResponse, authResponse);
        addTsAppMetricsToResponse(authResponse, response);
    }

    /**
     * @param authResponse
     * @param httpResponse
     */
    final void addTsAppMetricsToResponse(AuthorizeResponse authResponse, HttpServletResponse httpResponse) {
        if (!this.tsAppMetricsOnHeader) {
            return;
        }

        ApiUsageMetric hitsMetric = authResponse.firstHitsMetric();

        httpResponse.setHeader("X-FeatureRateLimit-Limit", hitsMetric.getMaxValue());
        httpResponse.setHeader("X-FeatureRateLimit-Remaining", String.valueOf(hitsMetric.getRemaining()));
        httpResponse.setHeader("X-FeatureRateLimit-Reset", String.valueOf(hitsMetric.getPeriodEndEpoch()));

    }

    /** */
    private static final String MISSING_API_ID_ERROR_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<error code=\"api_id_not_set\">app_id was not provided in the request</error>";

    /** */
    private static final String MISSING_API_ID_ERROR_JSON = "{ \"code\":\"api_id_not_set\", \"description\":\"app_id was not provided in the request\" }";

    /** */
    final String getMissingApiIdErrorMessag(HttpServletRequest httpRequest) {
        //get get the correct message according to the content-type, accepted encoding and file extension fo the request.
        //return MISSING_API_ID_ERROR_JSON;
        return MISSING_API_ID_ERROR_XML;
    }

    /**
     *
     */
    final static class ParamsFactory {

        public static final String DEFAULT_TS_APP_ID_HEADER = "X-App-Id";
        public static final String DEFAULT_TS_APP_KEY_HEADER = "X-App-Key";
        public static final String DEFAULT_TS_USER_KEY_HEADER = "X-User-Key";
        public static final String DEFAULT_TS_REFERRER_HEADER = "X-Referrer";

        public static final String DEFAULT_TS_APP_ID_QUERY = "app_id";
        public static final String DEFAULT_TS_APP_KEY_QUERY = "app_key";
        public static final String DEFAULT_TS_USER_KEY_QUERY = "user_key";
        public static final String DEFAULT_TS_REFERRER_QUERY = "referrer";

        private static String headerKey_ts_app_id = DEFAULT_TS_APP_ID_HEADER;
        private static String headerKey_ts_app_key = DEFAULT_TS_APP_KEY_HEADER;
        private static String headerKey_ts_user_key = DEFAULT_TS_USER_KEY_HEADER;
        private static String headerKey_ts_referrer = DEFAULT_TS_REFERRER_HEADER;

        private static String queryKey_ts_app_id = DEFAULT_TS_APP_ID_QUERY;
        private static String queryKey_ts_app_key = DEFAULT_TS_APP_KEY_QUERY;
        private static String queryKey_ts_user_key = DEFAULT_TS_USER_KEY_QUERY;
        private static String queryKey_ts_referrer = DEFAULT_TS_REFERRER_QUERY;


        static void init(FilterConfig config) {

            headerKey_ts_app_id = Helper.processInitParam(config, "ts_app_id_header_name", DEFAULT_TS_APP_ID_HEADER);
            headerKey_ts_app_key = Helper.processInitParam(config, "ts_app_key_header_name", DEFAULT_TS_APP_KEY_HEADER);
            headerKey_ts_user_key = Helper.processInitParam(config, "ts_user_key_header_name", DEFAULT_TS_USER_KEY_HEADER);
            headerKey_ts_referrer = Helper.processInitParam(config, "ts_referrer_header_name", DEFAULT_TS_REFERRER_HEADER);

            queryKey_ts_app_id = Helper.processInitParam(config, "ts_app_id_param_name", DEFAULT_TS_APP_ID_QUERY);
            queryKey_ts_app_key = Helper.processInitParam(config, "ts_app_key_param_name", DEFAULT_TS_APP_KEY_QUERY);
            queryKey_ts_user_key = Helper.processInitParam(config, "ts_user_key_param_name", DEFAULT_TS_USER_KEY_QUERY);
            queryKey_ts_referrer = Helper.processInitParam(config, "ts_referrer_param_name", DEFAULT_TS_REFERRER_QUERY);

        }


        static Params getFromRequest(HttpServletRequest request) {
            return new Params(
                    getValueFromHeader(request, headerKey_ts_app_id, request.getParameter(queryKey_ts_app_id)),
                    getValueFromHeader(request, headerKey_ts_app_key, request.getParameter(queryKey_ts_app_key)),
                    getValueFromHeader(request, headerKey_ts_user_key, request.getParameter(queryKey_ts_user_key)),
                    getValueFromHeader(request, headerKey_ts_referrer, request.getParameter(queryKey_ts_referrer))
            );
        }

        private static String getValueFromHeader(HttpServletRequest request, String name, String defaultValue) {
            String header = request.getHeader(name);
            if (header == null || header.trim().isEmpty()) {
                return defaultValue;
            }
            return header;
        }

    }

    /**
     *
     */
    final static class Params {
        final String appId;
        final String appKey;
        final String userKey;
        final String referrer;

        Params(String appId, String appKey, String userKey, String referrer) {
            this.appId = appId;
            this.appKey = appKey;
            this.userKey = userKey;
            this.referrer = referrer;
        }

        boolean hasAppId() {
            return appId != null;
        }

        boolean hasUserKey() {
            return userKey != null;
        }


        public String toString() {
            return new StringBuilder(this.getClass().getName())
                    .append("{")
                    .append(" appId:").append(appId).append(",")
                    .append(" appKey:").append(appKey).append(",")
                    .append(" userKey:").append(userKey).append(",")
                    .append(" referrer:").append(referrer).append(",")
                    .append("}").toString();
        }
    }

}
