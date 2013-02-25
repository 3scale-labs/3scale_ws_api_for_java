package threescale.v3.api;

import nu.xom.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public class AuthorizeResponse {

    private boolean status = false;
    private String plan = "";
    private String appKey = "";
    private UsageReport[] usageReports = new UsageReport[0];
    private String reason = "";
    private String errorCode = "";
    private String redirectUrl = "";

    public AuthorizeResponse(int httpStatus, String httpContent) throws ServerError {
        if (httpStatus == 200 || httpStatus == 409) {
            createAuthorizedOKOrExceeded(httpStatus, httpContent);
        } else {
            createAuthorizationFailed(httpStatus, httpContent);
        }

    }


    private void createAuthorizationFailed(int httpStatus, String httpContent) {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(httpContent, null);
            Element root = doc.getRootElement();

            final Attribute codeEl = root.getAttribute("code");
            setErrorCode(codeEl.getValue());
            setReason(root.getValue());
            setStatus("false");

        } catch (ParsingException ex) {
            System.err.println("Cafe con Leche is malformed today. How embarrassing!");
        } catch (IOException ex) {
            System.err.println("Could not connect to Cafe con Leche. The site may be down.");
        }
    }

    private void setErrorCode(String code) {
        errorCode = code;
    }

    private void createAuthorizedOKOrExceeded(int httpStatus, String httpContent) throws ServerError {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(httpContent, null);
            Element root = doc.getRootElement();

            Element authorizedEl = root.getFirstChildElement("authorized");
            setStatus(authorizedEl.getValue());
            if (success() == false) {
                Element reasonEl = root.getFirstChildElement("reason");
                if (reasonEl != null) {
                    setReason(reasonEl.getValue());
                }
            }

            Element planEl = root.getFirstChildElement("plan");
            setPlan(planEl.getValue());

            Element applicationEl = root.getFirstChildElement("application");
            if (applicationEl != null) {
                Element keyEl = applicationEl.getFirstChildElement("key");
                if (keyEl != null) {
                    setAppKey(keyEl.getValue());
                }

                Element redirectUrlEl = applicationEl.getFirstChildElement("redirect_url");
                if (redirectUrlEl != null) {
                    setRedirectUrl(redirectUrlEl.getValue());
                }
            } else {
                appKey = "";
                redirectUrl = "";
            }
            ArrayList<UsageReport> reports = new ArrayList<UsageReport>();
            Element usageReportsEl = root.getFirstChildElement("usage_reports");
            if (usageReportsEl != null) {
                Elements usageReports = usageReportsEl.getChildElements("usage_report");
                for (int upindex = 0; upindex < usageReports.size(); upindex++) {
                    processUsageReport(reports, usageReports.get(upindex));
                }
            }
            setUsageReports(reports);
            return;
        } catch (ParsingException ex) {
            throw new ServerError("The xml received was invalid: " + httpContent);
        } catch (IOException ex) {
            throw new ServerError("Unable to connection to 3scale server");
        }
    }

    private void processUsageReport(ArrayList<UsageReport> reports, Element usageEl) {
        final Attribute metricEl = usageEl.getAttribute("metric");
        final Attribute periodEl = usageEl.getAttribute("period");
        final Attribute exceededEl = usageEl.getAttribute("exceeded");
        final Element periodStartEl = usageEl.getFirstChildElement("period_start");
        final Element periodEndEl = usageEl.getFirstChildElement("period_end");
        final Element currentValue = usageEl.getFirstChildElement("current_value");
        final Element maxValue = usageEl.getFirstChildElement("max_value");

        reports.add(new UsageReport(metricEl.getValue(), periodEl.getValue(),
                periodStartEl.getValue(), periodEndEl.getValue(),
                currentValue.getValue(), maxValue.getValue(),
                (exceededEl == null) ? "false" : exceededEl.getValue()
        ));
    }
/*
<usage_report metric="hits" period="day">" +
                "      <period_start>2010-04-26 00:00:00 +0000</period_start>" +
                "      <period_end>2010-04-27 00:00:00 +0000</period_end>" +
                "      <current_value>10023</current_value>" +
                "      <max_value>50000</max_value>" +
                "    </usage_report>" +
 */

    public String getPlan() {
        return plan;
    }

    private void setPlan(String plan) {
        this.plan = plan;
    }

    public String getAppKey() {
        return appKey;
    }

    private void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public UsageReport[] getUsageReports() {
        return usageReports;
    }

    private void setUsageReports(ArrayList<UsageReport> reports) {
        usageReports = new UsageReport[reports.size()];
        usageReports = reports.toArray(new UsageReport[0]);
    }

    public boolean success() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }

    private void setStatus(String status) {
        if (status.toLowerCase().equals("true")) {
            this.status = true;
        } else {
            this.status = false;
        }
    }

    private void setReason(String reason) {
        this.reason = reason;
    }

    private void setRedirectUrl(String url) {
        this.redirectUrl = url;
    }

}
