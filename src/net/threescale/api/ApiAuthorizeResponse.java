package net.threescale.api;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Jul-2010
 * Time: 09:50:02
 */
public class ApiAuthorizeResponse {

    private Logger log = LogFactory.getLogger(this);

    private String plan = "";
    private ApiUsage[] usages = null;

    public ApiAuthorizeResponse(String responseFromServer) throws ApiException {

        XPathFactory xPathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
        XPath xpath = xPathFactory.newXPath();

        if (responseFromServer != null && responseFromServer.trim().length() != 0) {
            log.info("Extracting usage info");
            plan = XmlHelper.extractNode(xpath, "/status/plan", responseFromServer);
        } else {
            log.info("Xml string was empty");
            throw new ApiException(999, null);
        }
    }

    public String getPlan() {
        return plan;
    }

    public ApiUsage[] getUsages() {
        return usages;
    }
}
