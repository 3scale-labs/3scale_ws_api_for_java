package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface AuthorizeResponse {
    public String getPlan();

    public String getAppKey();

    public String getRedirectUrl();

    public UsageReport[] getUsageReports();

    public boolean success();

    public String getErrorCode();

    public String getErrorMessage();

}
