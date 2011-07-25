package net.threescale.api.servlet.filter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The filter sends back error responses to the user
 */
public class FilterRespondsToUser implements FilterResponseSelector {

    @Override
    public void sendFailedResponse(HttpServletResponse httpResponse, int httpStatus, String rawMessage) throws IOException {
        setStatusAndResponse(httpResponse, httpStatus, rawMessage);        
    }


    private void setStatusAndResponse(HttpServletResponse response, int code, String rawMessage)
      throws IOException {
        response.setStatus(code);
        PrintWriter writer = response.getWriter();
        writer.append(rawMessage);
        writer.flush();
    }


}
