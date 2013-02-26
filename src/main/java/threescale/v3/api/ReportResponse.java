package threescale.v3.api;

import nu.xom.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public class ReportResponse {
    private String errorCode = "";
    private String errorMessage = "";
    private boolean status = false;

    public ReportResponse(HtmlResponse response) throws ServerError {
        if (response.getStatus() == 200) {
            status = true;
        } else {
            status = false;
            parseResponse(response);
        }
    }

    private void parseResponse(HtmlResponse response) throws ServerError {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(response.getBody(), null);
            Element root = doc.getRootElement();

            Attribute codeEl = root.getAttribute("code");
            errorCode = codeEl.getValue();
            errorMessage = root.getValue();
            return;
        } catch (ParsingException ex) {
            throw new ServerError("The xml received was invalid: " + response.getBody());
        } catch (IOException ex) {
            throw new ServerError("Unable to connection to 3scale server");
        }
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean success() {
        return status;
    }
}
