package net.threescale.api.servlet.filter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows the response to a filter request to be selected. 
 */
public interface FilterResponseSelector {
    void sendFailedResponse(HttpServletResponse httpResponse, int httpStatus, String rawMessage) throws IOException;
}
