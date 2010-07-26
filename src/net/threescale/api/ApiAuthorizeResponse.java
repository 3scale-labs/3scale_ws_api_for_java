package net.threescale.api;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

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
            plan = extractPlan(responseFromServer, xpath);
            usages = extractUsages(responseFromServer, xpath);
        } else {
            log.info("Xml string was empty");
            throw new ApiException(999, null);
        }
    }


    private String extractPlan(String responseFromServer, XPath xpath) {
       return XmlHelper.extractNode(xpath, "/status/plan", responseFromServer);
    }

    private ApiUsage[] extractUsages(String responseFromServer, XPath xpath) {

        ArrayList<ApiUsage> results = new ArrayList<ApiUsage>();

        try {
            NodeList nodes = XmlHelper.extractNodeList(xpath, "/status/usage", responseFromServer);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node item = nodes.item(i);
                NodeList childNodes = item.getChildNodes();
                results.add(extractApiUsage(
                        item.getAttributes().getNamedItem("metric").getNodeValue(),
                        item.getAttributes().getNamedItem("period").getNodeValue(), 
                        childNodes));
            }
        }
        catch (Exception ex) {
          log.log(WARNING, ex.getMessage(), ex);
        }
        return results.toArray(new ApiUsage[0]);
    }

    private ApiUsage extractApiUsage(String metric, String period,NodeList childNodes) throws Exception {
        String periodStart = null;
        String periodEnd = null;
        String currentValue = null;
        String maxValue = null;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            String name = node.getNodeName();

            if (name.equals("period_start")) {
                periodStart = node.getFirstChild().getNodeValue();
            }
            else if (name.equals("period_end")) {
                periodEnd =  node.getFirstChild().getNodeValue();
            }
            else if (name.equals("current_value")) {
                currentValue =  node.getFirstChild().getNodeValue();
            }
            else if (name.equals("max_value")) {
                maxValue =  node.getFirstChild().getNodeValue();
            }
            else if (name.equals("#text")) {
            }
            else {
                throw new Exception("Unknown usage parameter: " + name );
            }
        }

        return new ApiUsage(metric, period, periodStart, periodEnd, currentValue, maxValue);
    }

    public String getPlan() {
        return plan;
    }

    public ApiUsage[] getUsages() {
        return usages;
    }
}
