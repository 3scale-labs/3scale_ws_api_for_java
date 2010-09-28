package net.threescale.api.v2;

import net.threescale.api.XmlHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 16:54:44
 */
public class ApiException extends Exception {
    private String errorCode;
    private String errorMessage;

    public ApiException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApiException(String xml) {
        XPathFactory xPathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
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
}
