package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface Client {
    String DEFAULT_HOST = "127.0.0.1";

    public AuthorizeResponse authrep(ParameterMap metrics);

    public ReportResponse report(ParameterMap... transactions);

    public AuthorizeResponse authorize(ParameterMap parameters);

    public String getHost();

    public AuthorizeResponse oauth_authorize(ParameterMap params);
}
