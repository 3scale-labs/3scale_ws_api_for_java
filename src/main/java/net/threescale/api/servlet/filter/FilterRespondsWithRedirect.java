package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;

import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Redirects the response to a supplied URL
 */
public class FilterRespondsWithRedirect implements FilterResponseSelector {
    private String redirect_url;
    private FilterConfig filterConfig;
    private String ts_authorize_response;

    public FilterRespondsWithRedirect(FilterConfig filterConfig, String redirect_url) {
        this.filterConfig = filterConfig;
        this.redirect_url = redirect_url;
        this.ts_authorize_response = Helper.processInitParam(filterConfig.getServletContext(), "ts_authorize_response_attr_name", "authorize_response");

    }

    @Override
    public void sendFailedResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int httpStatus, ApiResponse apiResponse) throws IOException, ServletException {

       httpRequest.getSession().setAttribute(ts_authorize_response, apiResponse);
        
        RequestDispatcher requestDispatcher = filterConfig.getServletContext().getRequestDispatcher(redirect_url);
        if (requestDispatcher != null) {
 	        requestDispatcher.forward(httpRequest, httpResponse);
        } else {
            httpResponse.setStatus(httpResponse.SC_MOVED_TEMPORARILY);
            httpResponse.setHeader("Location", redirect_url);
        }
    }
}
