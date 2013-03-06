package threescale.v3.api;

/**
 * Service API interface.
 */
public interface ServiceApi {
    String DEFAULT_HOST = "su1.3scale.net";

    /**
     * Performs and AuthRep operation
     *
     * @param metrics The app_id and metrics for this authrep
     * @return Information about the success/failure of the operation and the current metrics
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public AuthorizeResponse authrep(ParameterMap metrics) throws ServerError;

    /**
     * Performs an Authorize
     *
     * @param parameters App_id etc for the authorize
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError;

    /**
     * Perform an Authorize using OAuth.
     *
     * @param params Parameters for the authorize
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError;

    /**
     * Report a set of metrics.
     *
     * @param service_id
     * @param transactions The metrics to be reported
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public ReportResponse report(String service_id, ParameterMap... transactions) throws ServerError;

    /**
     * Get the URL of the 3scale server
     *
     * @return Server url
     */
    public String getHost();
}
