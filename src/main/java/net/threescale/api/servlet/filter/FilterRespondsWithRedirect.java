package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Redirects the response to a supplied URL
 */
public class FilterRespondsWithRedirect implements FilterResponseSelector {
    private String redirect_url;
    private String ts_authorize_response;
    private ServletContext servletContext;

    public FilterRespondsWithRedirect(ServletContext servletContext, String redirect_url) {
        this.servletContext = servletContext;
        this.redirect_url = redirect_url;
        this.ts_authorize_response = Helper.processInitParam(servletContext, "ts_authorize_response_attr_name", "authorize_response");

    }

    @Override
    public void sendFailedResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int httpStatus, ApiResponse apiResponse) throws IOException, ServletException {

       httpRequest.getSession().setAttribute(ts_authorize_response, apiResponse);
        
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(redirect_url);
        if (requestDispatcher != null) {
 	        requestDispatcher.forward(httpRequest, httpResponse);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            httpResponse.setHeader("Location", redirect_url);
        }
    }

    public ServerResponse sendFailedResponse(HttpServletRequest httpRequest, int status, ApiResponse response) {
        httpRequest.getSession().setAttribute(ts_authorize_response, response);

    Headers<Object> headers = new Headers<Object>();
        headers.add("Location", redirect_url);
        ServerResponse serverResponse = new ServerResponse(response.getRawMessage(), HttpServletResponse.SC_MOVED_TEMPORARILY, headers);
        return serverResponse;    }
}
