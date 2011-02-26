package net.threescale.api.v2;

import net.threescale.api.XmlHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Encapsulate Api Exceptions
 */
public class ApiException extends Exception {
    private String errorCode;
    private String errorMessage;

    /**
     * Constructor
     *
     * @param errorCode    HTTP response code.
     * @param errorMessage Error returned from server.
     */
    public ApiException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Contructor to build error from XML response.
     *
     * @param xml error xml.
     */
    public ApiException(String xml) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        NodeList nodes = XmlHelper.extractNodeList(xpath, "//error[@code]", xml);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node attr = node.getAttributes().getNamedItem("code");
            if (attr != null) {
                errorCode = attr.getNodeValue();
                break;
            }
        }
        errorMessage = XmlHelper.extractNode(xpath, "//error", xml);

    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int toHttpStatusCode() {
        if ("application_not_found".equals(errorCode)) {
            return 404;
        }
        //todo other errorCodes
        return 500;
    }

    public String toHttpStatusMessage() {
        if ("application_not_found".equals(errorCode)) {
            return "Application Not Found";
        }
        //todo other errorCodes
        return errorCode;
    }
}
