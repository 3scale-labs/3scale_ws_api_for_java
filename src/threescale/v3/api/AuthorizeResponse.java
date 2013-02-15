package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface AuthorizeResponse {
    public String getPlan();

    public String getAppKey();

    public String getRedirectUrl();
}
