package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The filter sends back error responses to the user
 */
public class FilterRespondsToUser implements FilterResponseSelector {

    @Override
    public void sendFailedResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int httpStatus, ApiResponse response) throws IOException, ServletException {
        setStatusAndResponse(httpResponse, httpStatus, response.getRawMessage());
    }


    private void setStatusAndResponse(HttpServletResponse response, int code, String rawMessage)
      throws IOException {
        response.setStatus(code);
        PrintWriter writer = response.getWriter();
        writer.append(rawMessage);
        writer.flush();
    }


}
