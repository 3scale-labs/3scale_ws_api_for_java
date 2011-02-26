package net.threescale.api;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Contains the reason information returned when an Api call fails.
 */
public class ApiException extends Exception {

    private static final long serialVersionUID = 2909145065587168842L;

    private int responseCode;
    private ApiError[] errors;

    public ApiException(int responseCode, String errorId, String errorMessage) {
        this.responseCode = responseCode;
        errors = new ApiError[1];
        errors[0] = new ApiError(errorId, 0, errorMessage);
    }

    public ApiException(int responseCode, String xmlMessage) {
        this.responseCode = responseCode;

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            if (xmlMessage != null && xmlMessage.length() > 0) {
                NodeList idList = XmlHelper.extractNodeList(xpath, "//error", xmlMessage);
                if (idList.getLength() == 1) {
                    errors = new ApiError[1];
                    errors[0] = new ApiError(
                            XmlHelper.extractNode(xpath, "//@id", xmlMessage),
                            0,
                            XmlHelper.extractNode(xpath, "//error", xmlMessage)
                    );
                } else {
                    errors = new ApiError[idList.getLength()];
                    for (int i = 0; i < idList.getLength(); i++) {
                        Node item = idList.item(i);
                        Node item1 = item.getAttributes().getNamedItem("id");
                        String id = item1.getNodeValue();
                        String index = item.getAttributes().getNamedItem("index").getNodeValue();
                        String msg = item.getFirstChild().getNodeValue();
                        errors[i] = new ApiError(id, Integer.valueOf(index), msg);
                    }
                }

            } else {
                this.responseCode = 500;
                errors = new ApiError[1];
                errors[0] = new ApiError("provider.other", 0, "xml error parsing response from server");
            }

        } catch (Exception e) {
            this.responseCode = 500;
            errors = new ApiError[1];
            errors[0] = new ApiError("provider.other", 0, "xml error parsing response from server");
            e.printStackTrace();
        }
    }

    public ApiError[] getErrors() {
        return errors;
    }


    /**
     * The response code from the underlaying Http call.
     *
     * @return The response code.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Get the error id.
     *
     * @return The error Id returned from the server.
     */
    public String getErrorId() {
        return (errors != null) ? errors[0].getId() : "";
    }

    /**
     * Get the human readable form of the message.
     *
     * @return The message.
     */
    public String getMessage() {
        return (errors != null) ? errors[0].getMessage() : "";
    }


    public int getErrorCount() {
        return (errors == null) ? 0 : errors.length;
    }

    public String toString() {
        return "[code:" + responseCode + " id:" + getErrorId() + ", message: " + getMessage() + "]";
    }
}
                            
