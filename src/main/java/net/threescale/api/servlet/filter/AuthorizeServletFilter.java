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
 *      <!-- optional.
 *          Name of the parameter in the query string that holds the Application Id, its overriding the default value.
 *          Defaults: app_id
 *          -->
 *      <init-param>
 *        <param-name>ts_app_id_param_name</param-name>
 *        <param-value>api_app_id</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the query string that holds the Application Key, its overriding the default value.
 *          Defaults: app_key
 *          -->
 *      <init-param>
 *        <param-name>ts_app_key_param_name</param-name>
 *        <param-value>api_app_key</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the query string that holds the API Referrer, its overriding the default value.
 *          Defaults: referrer
 *          -->
 *      <init-param>
 *        <param-name>ts_referrer_param_name</param-name>
 *        <param-value>api_referrer</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the header that holds the Application Id.
 *          -->
 *      <init-param>
 *          <param-name>ts_app_id_header_name</param-name>
 *          <param-value>X-App-Id</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the header that holds the Application Key.
 *          -->
 *      <init-param>
 *          <param-name>ts_app_key_header_name</param-name>
 *          <param-value>X-App-Key</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the header that holds the User Key.
 *          -->
 *      <init-param>
 *          <param-name>ts_user_key_header_name</param-name>
 *          <param-value>X-User-Key</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the parameter in the header that holds the Referrer.
 *          -->
 *      <init-param>
 *          <param-name>ts_referrer_header_name</param-name>
 *          <param-value>X-Referrer</param-value>
 *      </init-param>
 *      <!-- optional.
 *          Name of the session attribute that holds the 3Scale Authentication Response.
 *          -->
 *      <init-param>
 *        <param-name>ts_authorize_response_attr_name</param-name>
 *        <param-value>api_auth_response</param-value>
 *      </init-param>
 *      <!-- optional. If enabled the following metrics will be included in the Request Header
 *          X-FeatureRateLimit-Limit
 *          X-FeatureRateLimit-Remaining
 *          X-FeatureRateLimit-Reset
 *      -->
 *      <init-param>
 *        <param-name>ts_app_metrics_on_header</param-name>
 *        <param-value>true</param-value>
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
 *
 * Some additional notes on the parameters above...
 * <ul>
 *     <li>The {@value #INIT_PKEY_TS_PROVIDER_KEY} it the Key given by 3Scale to the owner of the API.</li>
 *     <li>The {@value #INIT_PKEY_TS_REDIRECT_URL} is the page the request is redirected to if an authorization error occurs. If
 *  this is not set the error information is returned to the user in the response body.</li>
 *     <li>The {@value #INIT_PKEY_TS_AUTH_RESPONSE_NAME}, or the value given to the corresponding <i>init-param</i>,
 *  is the attribute name that will contain the {@link AuthorizeResponse} or an {@link ApiException} depending on the type of failure.
 *  For a failed authorization due to limits exceeded etc. it will be an {@link AuthorizeResponse}, anything else will set an {@link ApiException}.</li>
 * </ul>
 * </p>
 * <p>
 * Then add a mapping for the request urls:
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
 *
 *    In this example the {@code ts_app_id_param_name}, {@code ts_app_key_param_name}, {@code ts_referrer_param_name} override the default names for the request parameters.
 * As mentioned in the comments if you omit them they default to: {@value #DEFAULT_TS_APP_ID_QUERY}, {@value #DEFAULT_TS_APP_KEY_QUERY} and {@value #DEFAULT_TS_REFERRER_QUERY}.
 * </p>
 * <p>
 *     <b>Note</b>: This filter can be disabled if the <i>System Flag</i> {@value #SYSTEM_FLAG_DISABLE_FILTERING} is set to "true".
 *     e.g.
 *     <pre>
 *     {@code
 *      java -Dnet.threescale.api.servlet.filter.disable=true
 *     }
 *     </pre>
 *
 *     Use this when you need to keep the same binary, e.g. war file, in different environments but you don't want to use 3Scale Authentication and Authorization services
 *     in everyone of them.
 *     Also note that if disabled a warning will be logged.
 * </p>
 */
public class AuthorizeServletFilter implements Filter {
    /** System Environment flag that if set will disable filtering, this should be used only for environments that do not
     * require 3Scale integration, flag name is {@value}*/
    public static final String SYSTEM_FLAG_DISABLE_FILTERING = "net.threescale.api.servlet.filter.disable";
    /** Default value of the 3Scale Service URL as defined by {@link ApiFactory#DEFAULT_3SCALE_PROVIDER_API_URL}. Value {@value net.threescale.api.ApiFactory#DEFAULT_3SCALE_PROVIDER_API_URL} */
    public static final String DEFAULT_TS_URL = ApiFactory.DEFAULT_3SCALE_PROVIDER_API_URL;
    /** Default name of the Class that will be used as the API Factory, should extends {@link ApiFactory}. Value {@value}  */
    public static final String DEFAULT_API_FACTORY_CLASS = "net.threescale.api.ApiFactory";
    /** Default name of the Attribute that will store the {@link AuthorizeResponse} in the {@link HttpSession}. Value {@value}  */
    public static final String DEFAULT_TS_AUTHORIZE_RESPONSE_ATTR_NAME = "authorize_response";
    /** Default value of the flag that triggers the addition of the usage metrics in the <i>Response Headers</i>. Value {@value} */
    public static final String DEFAULT_TS_APP_METRICS_ON_HEADER = "false";
    /** HTTP Header Key that holds the Feature Rate Limit - Limit Value. Value {@value} */
    public static final String RESPONSE_HEADER_USAGE_REPORT_FRL_LIMIT_KEY = "X-FeatureRateLimit-Limit";
    /** HTTP Header Key that holds the Feature Rate Limit - Current Value. Value {@value} */
    public static final String RESPONSE_HEADER_USAGE_REPORT_FRL_CURRENT_KEY = "X-FeatureRateLimit-Current";
    /** HTTP Header Key that holds the Feature Rate Limit - Remaining Values. Value {@value} */
    public static final String RESPONSE_HEADER_USAGE_REPORT_FRL_REMAINING_KEY =  "X-FeatureRateLimit-Remaining";
    /** HTTP Header Key that holds the Feature Rate Limit - Period Start Epoch. Value {@value} */
    public static final String RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_START_EPOCH_KEY = "X-FeatureRateLimit-Period-Start-Epoch";
    /** HTTP Header Key that holds the Feature Rate Limit - Period End Epoch. Value {@value} */
    public static final String RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_END_EPOCH_KEY = "X-FeatureRateLimit-Period-End-Epoch";
    /** INIT-PARAM Key to specify 3Scale URL. Value {@value} */
    public static final String INIT_PKEY_TS_URL = "ts_url";
    /** INIT-PARAM Key to specify the <i>on authentication failure</i> redirection. Value {@value} */
    public static final String INIT_PKEY_TS_REDIRECT_URL = "ts_redirect_url";
    /** INIT-PARAM Key to specify the name of the attribute holding the {@link AuthorizeResponse}. Value {@value} */
    public static final String INIT_PKEY_TS_AUTH_RESPONSE_NAME = "ts_authorize_response_attr_name";
    /** INIT-PARAM Key to enable Usage Metrics on the Response. Value {@value} */
    public static final String INIT_PKEY_TS_APP_METRICS_ON_HEADER = "ts_app_metrics_on_header";
    /** INIT-PARAM Key for 3Scale Provider Key. Value {@value} */
    public static final String INIT_PKEY_TS_PROVIDER_KEY = "ts_provider_key";
    /** INIT-PARAM Key that specifies the HTTP Header Attribute that references the 3Scale App ID, defaults to {@value} */
    public static final String INIT_PKEY_TS_APP_ID_HEADER_NAME = "ts_app_id_header_name";
    /** INIT-PARAM Key that specifies the HTTP Header Attribute that references the 3Scale App Key, defaults to {@value} */
    public static final String INIT_PKEY_TS_APP_KEY_HEADER_NAME = "ts_app_key_header_name";
    /** INIT-PARAM Key that specifies the HTTP Header Attribute that references the 3Scale User Key, defaults to {@value} */
    public static final String INIT_PKEY_TS_USER_KEY_HEADER_NAME =  "ts_user_key_header_name";
    /** INIT-PARAM Key that specifies the HTTP Header Attribute that references the <i>Referrer</i> used for authentication, defaults to {@value} */
    public static final String INIT_PKEY_TS_REFERRER_HEADER_NAME =  "ts_referrer_header_name";
    /** INIT-PARAM Key that specifies the HTTP Query Parameter that references the 3Scale App ID, defaults to {@value} */
    public static final String INIT_PKEY_TS_APP_ID_PARAM_NAME =  "ts_app_id_param_name";
    /** INIT-PARAM Key that specifies the HTTP Query Parameter that references the 3Scale App Key, defaults to {@value} */
    public static final String INIT_PKEY_TS_APP_KEY_PARAM_NAME =  "ts_app_key_param_name";
    /** INIT-PARAM Key that specifies the HTTP Query Parameter that references the 3Scale User Key, defaults to {@value} */
    public static final String INIT_PKEY_TS_USER_KEY_PARAM_NAME =  "ts_user_key_param_name";
    /** INIT-PARAM Key that specifies the HTTP Query Parameter that references the <i>Referrer</i> used for authentication, defaults to {@value} */
    public static final String INIT_PKEY_TS_REFERRER_PARAM_NAME =  "ts_referrer_param_name";
    /** Default name of the Header Attribute that contains the Application Id, value {@value}*/
    public static final String DEFAULT_TS_APP_ID_HEADER = "X-App-Id";
    /** Default name of the Header Attribute that contains the Application Id, value {@value}*/
    public static final String DEFAULT_TS_APP_KEY_HEADER = "X-App-Key";
    /** Default name of the Header Attribute that contains the User Key, value {@value}*/
    public static final String DEFAULT_TS_USER_KEY_HEADER = "X-User-Key";
    /** Default name of the Header Attribute that contains the Referrer, value {@value}*/
    public static final String DEFAULT_TS_REFERRER_HEADER = "X-Referrer";
    /** Default name of the Query Parameter that contains the Application Id, value {@value}*/
    public static final String DEFAULT_TS_APP_ID_QUERY = "app_id";
    /** Default name of the Query Parameter that contains the App Key, value {@value}*/
    public static final String DEFAULT_TS_APP_KEY_QUERY = "app_key";
    /** Default name of the Query Parameter that contains the User Key, value {@value}*/
    public static final String DEFAULT_TS_USER_KEY_QUERY = "user_key";
    /** Default name of the Query Parameter that contains the Referrer, value {@value}*/
    public static final String DEFAULT_TS_REFERRER_QUERY = "referrer";


    private static Class<? extends ApiFactory> factoryClass;

    static {
        try {
            AuthorizeServletFilter.factoryClass = getFactoryClass(DEFAULT_API_FACTORY_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load default Factory Class:" + DEFAULT_API_FACTORY_CLASS, e);
        }
    }


    private volatile boolean systemFlagDisableFiltering;

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
     * Sets the API Factory, the Class implementing the factory must be available through the classpath when this method
     * is called.
     * @param
     */
    public static final void setApiFactory(String factoryClassName) throws ClassNotFoundException {
        AuthorizeServletFilter.factoryClass = AuthorizeServletFilter.getFactoryClass(factoryClassName);
    }

    /**
     * Sets the API Factory.
     * @param
     */
    public static final void setApiFactory(Class<? extends ApiFactory> factoryClass) {
        AuthorizeServletFilter.factoryClass = factoryClass;
    }


    /**
     * Initialises the Filter by ..
     * <ol>
     *     <li>Checks if the {@value #SYSTEM_FLAG_DISABLE_FILTERING} is set</li>
     *     <li>Loading the Parameters specified in the WEB-INF/web.mxl</li>
     *     <li>Initializes the Response Templates.</li>
     *     <li>Initializes the 3Scale API Server stub.</li>
     *     <li>Initializes the Parameter Factory.</li>
     * </ol>
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


        this.config = filterConfig;

        this.context = filterConfig.getServletContext();

        checkSystemDisabledFlag(config, context, false);

        processInitParams();

        initFilterResponse();

        initiateApiServer();

        ParamsFactory.init(filterConfig);
    }

    private void checkSystemDisabledFlag(final FilterConfig config,
                                         final ServletContext context,
                                         final boolean defaultValue){
        final String flag = System.getProperty( SYSTEM_FLAG_DISABLE_FILTERING );
        if ( flag == null ){
            this.systemFlagDisableFiltering =  defaultValue;
        } else {
            this.systemFlagDisableFiltering = Boolean.valueOf(flag);
            context.log( String.format( "The filter %s will be disabled since the System Property %s is set.",
                    config.getFilterName(), SYSTEM_FLAG_DISABLE_FILTERING) );
        }
    }


    /**
     * Checks if the filter is enabled through the System Flag '{@value #SYSTEM_FLAG_DISABLE_FILTERING}' and if so
     * it does and an <i>Authentication and Authorization</i> request through {@link #authenticateAndAuthorize}.
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        if (this.systemFlagDisableFiltering){
            filterChain.doFilter(servletRequest, servletResponse);
        }  else {
            authenticateAndAuthorize(servletRequest, servletResponse, filterChain);
        }

    }

    /**
     * Authorizes the {@link HttpServletRequest} according to the <i>Application ID</i>, <i>Application Key</i> and <i>Referrer</i>
     * or <i>User Key</i> and <i>Referrer</i>. If the authorization succeeds the <i>Filter Chain</i> continues else
     * it stops.
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    public final void authenticateAndAuthorize(final ServletRequest servletRequest,
                                               final ServletResponse servletResponse,
                                               final FilterChain filterChain) throws IOException, ServletException {


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
            context.log("Request doesn't have an App ID or User Key.");

            filterResponse.sendFailedResponse(httpRequest,
                    httpResponse, 404, new ApiException(getMissingApiIdErrorMessag(httpRequest)));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }

    /**
     * Reads from the {@link FilterConfig} the <i>init-parms</i> that specify...
     * <ul>
     *     <li>{@value #INIT_PKEY_TS_URL}: 3Scale service URL, defaults to the value given to {@link #DEFAULT_TS_URL}</li>
     *     <li>{@value #INIT_PKEY_TS_REDIRECT_URL}: URL to redirect the clients when the authorization process fails.</li>
     *     <li>{@value #INIT_PKEY_TS_AUTH_RESPONSE_NAME}: The name of the session attribute pointing to the {@link AuthorizeResponse}</li>
     *     <li>{@value #INIT_PKEY_TS_APP_METRICS_ON_HEADER}: If true metric stats will be included in the <i>HTTP Response Header</i>.</li>
     *     <li>{@value #INIT_PKEY_TS_PROVIDER_KEY}: 3Scale Provider Key</li>
     * </ul>
     * @throws ServletException
     */
    final void processInitParams() throws ServletException {

        tsUrl = Helper.processInitParam(config, INIT_PKEY_TS_URL, DEFAULT_TS_URL);

        tsRedirectUrl = Helper.processInitParam(config, INIT_PKEY_TS_REDIRECT_URL, null);

        tsAuthorizeResponse = Helper.processInitParam(config, INIT_PKEY_TS_AUTH_RESPONSE_NAME, DEFAULT_TS_AUTHORIZE_RESPONSE_ATTR_NAME);

        tsAppMetricsOnHeader = Boolean.parseBoolean(Helper.processInitParam(config, INIT_PKEY_TS_APP_METRICS_ON_HEADER, DEFAULT_TS_APP_METRICS_ON_HEADER));

        tsProviderKey = Helper.processInitParam(config, INIT_PKEY_TS_PROVIDER_KEY, null);

        if (tsProviderKey == null) {
            throw new ServletException("No provider key has been set for AuthorizeServeltFilter");
        }
    }


    /**
     * Obtains a session and clean any previous {@link AuthorizeResponse}.
     * @param request
     * @return
     */
    final HttpSession getSession(HttpServletRequest request) {

        HttpSession session = request.getSession();

        session.removeAttribute(tsAuthorizeResponse);

        return session;
    }

    /**
     * Initializes the Response Template to either use a <i>redirect</i> defined by the <i>ts_redirect_url</i> init-param
     * our output the error direclty.
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
     * Creates the 3Scale API Server according to the API Factory defined by {@link #factoryClass}.
     * Override if there is different process that you need to use to create the {@link Api2} reference.
     * @return API Server
     */
    protected Api2 createApi2Server() {
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
     * Initiates the API Server as defined by the {@link #createApi2Server}. This method is guarded by the {@link #serverLock}
     * and requires an initialized context. i.e. The {@link #init(javax.servlet.FilterConfig)} needs to be executed before
     * this method is called, if not a {@link RuntimeException} will be thrown.
     */
    final void initiateApiServer() {
        ServletContext _context = this.context;
        if (_context == null){
            throw new IllegalStateException("The Filter has not been initialized.");
        }

        serverLock.tryLock();
        try {
            server = createApi2Server();

            _context.log(String.format("Create server object with url:[%s] and provider_key [%s]", tsUrl, tsProviderKey));
        } catch (Exception ex) {
            _context.log("Could not create API object for 3scale interface", ex);
        } finally {
            serverLock.unlock();
        }
    }

    /**
     * Attempts an <i>authorization</i> of the given <i>App Id</i>, <i>App Key</i> and <i>Referrer</i> using the <i>Server</i> provided by the {@link #getServer()} method.
     * @param appId Application Identifier
     * @param appKey Application Key
     * @param referrer API Referrer
     * @return 3Scale Authorization Response
     * @throws ApiException
     */
    protected AuthorizeResponse authorizeForApp(final String appId, final String appKey, final String referrer) throws ApiException {
        return this.getServer().authorize(appId, appKey, referrer, null);
    }

    /**
     * Attempts an <i>authorization</i> of the given <i>User Key</i> and <i>Referrer</i> using the <i>Server</i> provided by the {@link #getServer()} method.
     * @param userKey User Key
     * @param referrer API Referrer
     * @return 3Scale Authorization Response
     * @throws ApiException
     */
    protected AuthorizeResponse authorizeForUser(final String userKey, final String referrer) throws ApiException {
         return this.getServer().authorizeWithUserKey(userKey, referrer, null);
    }

    /**
     * Guarded by a lock, it securely obtains a reference to the {@link Api2} <i>Server</i>.
     * @return the API Server, will throw a {@link RuntimeException} if unable to do so.
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
     * Process the Authorize Response by adding values to...
     * <ul>
     *     <li>Session Attribute that stores the {@link AuthorizeResponse}</li>
     *     <li>Adds, if configured, the <i>Hits Metrics</i> to the {@link HttpServletResponse}.</li>
     * </ul>
     * @param authResponse 3Scales Authorization & Authentication Response
     * @param session   Http Session
     * @param response Http Response
     */
    final void processAuthorizationResponse(final AuthorizeResponse authResponse,
                                            final HttpSession session,
                                            final HttpServletResponse response) {

        session.setAttribute(tsAuthorizeResponse, authResponse);
        addTsAppMetricsToResponse(authResponse, response);
    }

    /**
     * Adds the 3Scale Authentication Response Hits Metrics values to the {@link HttpServletResponse}
     * @param authResponse 3Scale's Authentication Response
     * @param httpResponse Http Response
     */
    final void addTsAppMetricsToResponse(AuthorizeResponse authResponse, HttpServletResponse httpResponse) {
        if (! this.tsAppMetricsOnHeader) {
            return;
        }

        ApiUsageMetric hitsMetric = authResponse.firstHitsMetric();

        httpResponse.setHeader(RESPONSE_HEADER_USAGE_REPORT_FRL_LIMIT_KEY, hitsMetric.getMaxValue());
        httpResponse.setHeader(RESPONSE_HEADER_USAGE_REPORT_FRL_CURRENT_KEY, String.valueOf(hitsMetric.getCurrentValue()));
        httpResponse.setHeader(RESPONSE_HEADER_USAGE_REPORT_FRL_REMAINING_KEY, String.valueOf(hitsMetric.getRemaining()));
        httpResponse.setHeader(RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_START_EPOCH_KEY, String.valueOf(hitsMetric.getPeriodStartEpoch()));
        httpResponse.setHeader(RESPONSE_HEADER_USAGE_REPORT_FRL_PERIOD_END_EPOCH_KEY, String.valueOf(hitsMetric.getPeriodEndEpoch()));
    }

    /** XML Error Message */
    private static final String MISSING_API_ID_ERROR_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<error code=\"api_id_not_set\">app_id was not provided in the request</error>";

    /** JSON Error Message */
    private static final String MISSING_API_ID_ERROR_JSON = "{ \"code\":\"api_id_not_set\", \"description\":\"app_id was not provided in the request\" }";

    /** */
    final String getMissingApiIdErrorMessag(HttpServletRequest httpRequest) {
        //get get the correct message according to the content-type, accepted encoding and file extension fo the request.
        //return MISSING_API_ID_ERROR_JSON;
        return MISSING_API_ID_ERROR_XML;
    }

    /**
     * Creates the Parameter Structure according the {@link HttpServletRequest} and the parameters provided in the
     * <i>header</i> or <i>query</i> as specified by either the <i>default</i> key names or the ones configured through
     * the <i>init-params</i>.
     *
     * @see #init(javax.servlet.FilterConfig)
     * @see #getFromRequest(javax.servlet.http.HttpServletRequest)
     */
    final static class ParamsFactory {


        private static String headerKey_ts_app_id = DEFAULT_TS_APP_ID_HEADER;
        private static String headerKey_ts_app_key = DEFAULT_TS_APP_KEY_HEADER;
        private static String headerKey_ts_user_key = DEFAULT_TS_USER_KEY_HEADER;
        private static String headerKey_ts_referrer = DEFAULT_TS_REFERRER_HEADER;

        private static String queryKey_ts_app_id = DEFAULT_TS_APP_ID_QUERY;
        private static String queryKey_ts_app_key = DEFAULT_TS_APP_KEY_QUERY;
        private static String queryKey_ts_user_key = DEFAULT_TS_USER_KEY_QUERY;
        private static String queryKey_ts_referrer = DEFAULT_TS_REFERRER_QUERY;


        private ParamsFactory(){}

        /**
         * <p>
         *  Extracts from the {@link FilterConfig} the names of the keys that will be used to fetch the
         *  <i>Application Id</i>, <i>Application Key</i>, <i>User Key</i> and <i>Referrer</i>.
         * </p>
         * <p>
         *   Default Values as defined bellow...
         *   <b>From Header</b>
         *   <ul>
         *      <li>Application Id: {@value ParamsFactory#DEFAULT_TS_APP_ID_HEADER}</li>
         *      <li>Application Key: {@value ParamsFactory#DEFAULT_TS_APP_KEY_HEADER}</li>
         *      <li>Application User Key: {@value ParamsFactory#DEFAULT_TS_USER_KEY_HEADER}</li>
         *      <li>Application Referrer: {@value ParamsFactory#DEFAULT_TS_REFERRER_HEADER}</li>
         *   </ul>
         * </p>
         * <p>
         *  <b>From Query</b>
         *  <ul>
         *      <li>Application Id: {@value ParamsFactory#DEFAULT_TS_APP_ID_QUERY}</li>
         *      <li>Application Key: {@value ParamsFactory#DEFAULT_TS_APP_KEY_QUERY}</li>
         *      <li>Application User Key: {@value ParamsFactory#DEFAULT_TS_USER_KEY_QUERY}</li>
         *      <li>Application Referrer: {@value ParamsFactory#DEFAULT_TS_REFERRER_QUERY}</li>
         *  </ul>
         * </p>
         */
        static void init(FilterConfig config) {

            headerKey_ts_app_id = Helper.processInitParam(config, INIT_PKEY_TS_APP_ID_HEADER_NAME, DEFAULT_TS_APP_ID_HEADER);
            headerKey_ts_app_key = Helper.processInitParam(config, INIT_PKEY_TS_APP_KEY_HEADER_NAME, DEFAULT_TS_APP_KEY_HEADER);
            headerKey_ts_user_key = Helper.processInitParam(config, INIT_PKEY_TS_USER_KEY_HEADER_NAME, DEFAULT_TS_USER_KEY_HEADER);
            headerKey_ts_referrer = Helper.processInitParam(config, INIT_PKEY_TS_REFERRER_HEADER_NAME, DEFAULT_TS_REFERRER_HEADER);

            queryKey_ts_app_id = Helper.processInitParam(config, INIT_PKEY_TS_APP_ID_PARAM_NAME, DEFAULT_TS_APP_ID_QUERY);
            queryKey_ts_app_key = Helper.processInitParam(config, INIT_PKEY_TS_APP_KEY_PARAM_NAME, DEFAULT_TS_APP_KEY_QUERY);
            queryKey_ts_user_key = Helper.processInitParam(config, INIT_PKEY_TS_USER_KEY_PARAM_NAME, DEFAULT_TS_USER_KEY_QUERY);
            queryKey_ts_referrer = Helper.processInitParam(config, INIT_PKEY_TS_REFERRER_PARAM_NAME, DEFAULT_TS_REFERRER_QUERY);

        }

        /**
         * Extracts from the request, either through the header or query, the parameters that are used for the 3Scale Auth.
         * @param request
         * @return reference holding the parameters.
         * @see Params
         */
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
     * Structure that holds the parameters used for the 3Scale Authentication and Authorization process.
     */
    final static class Params {
        /** Application Identifier */
        public final String appId;
        /** Application API Key */
        public final String appKey;
        /** User Key */
        public final String userKey;
        /** Request Referrer */
        public final String referrer;

        Params(String appId, String appKey, String userKey, String referrer) {
            this.appId = appId;
            this.appKey = appKey;
            this.userKey = userKey;
            this.referrer = referrer;
        }

        public boolean hasAppId() {
            return appId != null;
        }

        public boolean hasUserKey() {
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
