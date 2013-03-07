package threescale.v3.api;

import nu.xom.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Provides information about the success or failure of an Authorize operation.
 * <p/>
 * The success response sets:
 * the Status (true = success false = exceeded/failed)
 * the Reason if failed.
 * the Plan
 * the ClientSecret
 * the redirect URL
 * the Usage reports
 * <p/>
 * The failure response sets:
 * the ErrorCode
 * Reason.
 */
public class AuthorizeResponse {

    private boolean status = false;
    private String plan = "";
    private String clientSecret = "";
    private UsageReport[] usageReports = new UsageReport[0];
    private String reason = "";
    private String errorCode = "";
    private String redirectUrl = "";

    /**
     * Build an AuthorizeResponse using the status and content of an html get.
     *
     * @param httpStatus  Status value from the GET
     * @param httpContent Contents of the GET
     * @throws ServerError If the received XML is invalid, or cannot process the XML
     */
    public AuthorizeResponse(int httpStatus, String httpContent) throws ServerError {
        if (httpStatus == 200 || httpStatus == 409) {
            createAuthorizedOKOrExceeded(httpContent);
        } else {
            createAuthorizationFailed(httpContent);
        }
    }

    /**
     * Create a failure response.
     *
     * @param httpContent
     * @throws ServerError
     */
    private void createAuthorizationFailed(String httpContent) throws ServerError {
        try {
            Builder parser = new Builder();
            Document doc = parser.build(httpContent, null);
            Element root = doc.getRootElement();

            final Attribute codeEl = root.getAttribute("code");
            setErrorCode(codeEl.getValue());
            setReason(root.getValue());
            setStatus("false");

        } catch (ParsingException ex) {
            throw new ServerError("The xml received was invalid: " + httpContent);
        } catch (IOException ex) {
            throw new ServerError("Error processing the XML");
        }
    }

    /**
     * Creates a success response
     *
     * @param httpContent
     * @throws ServerError
     */
    private void createAuthorizedOKOrExceeded(String httpContent) throws ServerError {
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
                    setClientSecret(keyEl.getValue());
                }

                Element redirectUrlEl = applicationEl.getFirstChildElement("redirect_url");
                if (redirectUrlEl != null) {
                    setRedirectUrl(redirectUrlEl.getValue());
                }
            } else {
                clientSecret = "";
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
            throw new ServerError("Error processing the XML");
        }
    }

    /**
     * Parse and build a usage report.
     *
     * @param reports
     * @param usageEl
     */
    private void processUsageReport(ArrayList<UsageReport> reports, Element usageEl) {
        final Attribute metricEl = usageEl.getAttribute("metric");
        final Attribute periodEl = usageEl.getAttribute("period");
        final Attribute exceededEl = usageEl.getAttribute("exceeded");
        final Element periodStartEl = usageEl.getFirstChildElement("period_start");
        final Element periodEndEl = usageEl.getFirstChildElement("period_end");
        final Element currentValue = usageEl.getFirstChildElement("current_value");
        final Element maxValue = usageEl.getFirstChildElement("max_value");

        reports.add(new UsageReport(getValueOrBlank(metricEl), getValueOrBlank(periodEl),
                getValueOrBlank(periodStartEl), getValueOrBlank(periodEndEl),
                getValueOrBlank(currentValue), getValueOrBlank(maxValue),
                (exceededEl == null) ? "false" : exceededEl.getValue()
        ));
    }


    /**
     * Get the name of the Plan
     *
     * @return Plan name
     */
    public String getPlan() {
        return plan;
    }

    private void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * Get the ClientSecret
     *
     * @return app key
     */
    public String getClientSecret() {
        return clientSecret;
    }

    private void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Get the redirect url
     *
     * @return redirect url
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Get the usage reports for this authoize
     *
     * @return
     */
    public UsageReport[] getUsageReports() {
        return usageReports;
    }

    private void setUsageReports(ArrayList<UsageReport> reports) {
        usageReports = new UsageReport[reports.size()];
        usageReports = reports.toArray(new UsageReport[0]);
    }

    /**
     * Get the status
     *
     * @return true / false
     */
    public boolean success() {
        return status;
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    private void setErrorCode(String code) {
        errorCode = code;
    }

    /**
     * Get the reason for the failure
     *
     * @return reason
     */
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

    private String getValueOrBlank(Attribute attr) {
        if (attr == null) {
            return "";
        } else {
            return attr.getValue();
        }
    }

    private String getValueOrBlank(Element element) {
        if (element == null) {
            return "";
        } else {
            return element.getValue();
        }
    }


}
