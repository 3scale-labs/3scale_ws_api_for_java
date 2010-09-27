package net.threescale.api.v2;

import net.threescale.api.LogFactory;
import net.threescale.api.XmlHelper;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:51:11
 */
public class ApiResponse {

    private Logger log = LogFactory.getLogger(this);

    private Boolean authorized = new Boolean(false);
    private String plan = "";

    public ApiResponse(String xml) {

        XPathFactory xPathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
        XPath xpath = xPathFactory.newXPath();

        authorized = new Boolean(XmlHelper.extractNode(xpath, "//status/authorized", xml));
        plan = XmlHelper.extractNode(xpath, "//status/plan", xml);
    }
    
    public boolean getAuthorized() {
        return authorized;
    }

    public ArrayList<ApiUsageMetric> getUsageReports() {
        return new ArrayList<ApiUsageMetric>();
    }

    public String getReason() {
        return "";
    }

    public String getPlan() {
        return plan; 
    }
}
