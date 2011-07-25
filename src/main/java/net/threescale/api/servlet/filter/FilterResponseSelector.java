package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;

import javax.servlet.ServletException;
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
}
