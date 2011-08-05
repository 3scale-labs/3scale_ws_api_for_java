package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;
import net.threescale.api.v2.AuthorizeResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows the response to a filter request to be selected. 
 */
public interface FilterResponseSelector {

    void sendFailedResponse(HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse,
                            int httpStatus,
                            ApiResponse apiResponse) throws IOException, ServletException;

    ServerResponse sendFailedResponse(HttpServletRequest httpRequest, int status, ApiResponse response);
}
