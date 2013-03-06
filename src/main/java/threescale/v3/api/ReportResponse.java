package threescale.v3.api;

import nu.xom.*;

import java.io.IOException;

/**
 * The response information from a Report operations.
 * success is true if the report succeeded.
 * success is false if it failed and the Error Code and Error Message fields will be populated.
 */
public class ReportResponse {
    private String errorCode = "";
    private String errorMessage = "";
    private boolean status = false;

    /**
     * Create a ReportResponse from an HTML POST
     *
     * @param response
     * @throws ServerError
     */
    public ReportResponse(HttpResponse response) throws ServerError {
        if (response.getStatus() == 200 || response.getStatus() == 202) {
            status = true;
        } else {
            status = false;
            parseResponse(response);
        }
    }

    private void parseResponse(HttpResponse response) throws ServerError {
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
            throw new ServerError("Unable process the XML");
        }
    }

    /**
     * Return the Error Code
     *
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Return the Error Message
     *
     * @return message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Return success / failure
     *
     * @return true = success, false = failure
     */
    public boolean success() {
        return status;
    }
}
