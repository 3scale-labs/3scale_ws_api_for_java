package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public class AuthorizeResponse {
    public String getPlan() {
        return "";
    }

    public String getAppKey() {
        return "";
    }

    public String getRedirectUrl() {
        return "";
    }

    public UsageReport[] getUsageReports() {
        return new UsageReport[0];
    }

    public boolean success() {
        return false;
    }

    public String getErrorCode() {
        return "";
    }

    public String getErrorMessage() {
        return "";
    }

}
