package net.threescale.api.servlet.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class intercepts the incoming request and checks for a parameter containing either the users
 * api key (ts_api_key) or app id (ts_app_id).
 *
 * If no key/id is present, or does not authorize correctly it returns an error response or xxxx.
 *
 * If the key/id does authorize the next filter in the chain is called.
 *
 * The parameter name for the api key (ts_api_key) or app id (ts_app_id) may be overridden in the
 * configuration.
 *
 */
public class AuthorizeServletFilter implements Filter {

    private FilterConfig filterConfig;
    private String ts_api_key = "ts_api_key";
    private ServletContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String api_key = servletRequest.getParameter(ts_api_key);
        if (api_key != null) {
            if (api_key.equals("23454321")) {
                context.log("Authorized ok for : " + api_key);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                context.log("Authorize failed for: " + api_key);
                ((HttpServletResponse) servletResponse).setStatus(409);
            }
        } else {
            context.log("api_key missing in request");
            ((HttpServletResponse) servletResponse).setStatus(409);
        }

    }

    @Override
    public void destroy() {
    }
}
