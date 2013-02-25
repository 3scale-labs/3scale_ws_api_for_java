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
            System.err.println("Cafe con Leche is malformed today. How embarrassing!");
        } catch (IOException ex) {
            System.err.println("Could not connect to Cafe con Leche. The site may be down.");
        }
        throw new ServerError("error");
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
