package net.threescale.api.servlet.filter;

import net.threescale.api.v2.ApiResponse;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * The filter sends back error responses to the user
 */
public class FilterRespondsToUser implements FilterResponseSelector {

    @Override
    public void sendFailedResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int httpStatus, ApiResponse response) throws IOException, ServletException {
        httpResponse.setStatus(httpStatus);
        httpResponse.setContentType("text/xml");
        PrintWriter writer = httpResponse.getWriter();
        writer.append(response.getRawMessage());
        writer.flush();
    }

    @Override
    public ServerResponse sendFailedResponse(HttpServletRequest httpRequest, int status, ApiResponse response) {

        Headers<Object> headers = new Headers<Object>();

        Enumeration headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            headers.add(headerName, httpRequest.getHeader(headerName));
        }

        headers.add("content-type", "text/xml");

        return new ServerResponse(response.getRawMessage(), status, headers);
    }


}
