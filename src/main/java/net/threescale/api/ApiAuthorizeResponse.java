package net.threescale.api;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * Response returned by server to authorize message.
 */
public class ApiAuthorizeResponse {

    private Logger log = LogFactory.getLogger(this);

    private String plan = "";
    private ApiUsageMetric[] usages = null;

    public ApiAuthorizeResponse(String responseFromServer) throws ApiException {


        if (responseFromServer != null && responseFromServer.trim().length() != 0) {

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            log.info("Extracting usage info");
            plan = extractPlan(responseFromServer, xpath);
            usages = extractUsages(responseFromServer, xpath);
        } else {
            log.info("Result was empty");
            throw new ApiException(500, "provider.other", "Result was empty");
        }
    }


    private String extractPlan(String responseFromServer, XPath xpath) {
       return XmlHelper.extractNode(xpath, "/status/plan", responseFromServer);
    }

    private ApiUsageMetric[] extractUsages(String responseFromServer, XPath xpath) {

        ArrayList<ApiUsageMetric> results = new ArrayList<ApiUsageMetric>();

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
        return results.toArray(new ApiUsageMetric[0]);
    }

    private ApiUsageMetric extractApiUsage(String metric, String period,NodeList childNodes) throws Exception {
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

        return new ApiUsageMetric(metric, period, periodStart, periodEnd, currentValue, maxValue);
    }

    /**
     * @return Plan name for this user key
     */
    public String getPlan() {
        return plan;
    }

    /**
     * @return Current Api usage. A zero length array is returned if there is no usage information.
     */
    public ApiUsageMetric[] getUsages() {
        return usages;
    }
}
