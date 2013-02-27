package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface ServiceApi {
    String DEFAULT_HOST = "su1.3scale.net";

    public AuthorizeResponse authrep(ParameterMap metrics) throws ServerError;

    public ReportResponse report(ParameterMap... transactions) throws ServerError;

    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError;

    public String getHost();

    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError;
}
