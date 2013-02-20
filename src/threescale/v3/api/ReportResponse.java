package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface ReportResponse {
    public String getErrorCode();

    public String getErrorMessage();

    public boolean success();
}
