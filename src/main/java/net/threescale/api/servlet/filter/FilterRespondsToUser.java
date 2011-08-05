package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;

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

    @Override
    public ServerResponse sendFailedResponse(HttpServletRequest httpRequest, int status, ApiResponse response) {

        Headers<Object> headers = new Headers<Object>();
        ServerResponse serverResponse = new ServerResponse(response.getRawMessage(), status, headers);
        return serverResponse;
    }


    private void setStatusAndResponse(HttpServletResponse response, int code, String rawMessage)
      throws IOException {
        response.setStatus(code);
        PrintWriter writer = response.getWriter();
        writer.append(rawMessage);
        writer.flush();
    }
}
