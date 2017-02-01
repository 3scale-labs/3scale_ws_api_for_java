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
	public AuthorizeResponse authrep(String serviceToken, String serviceId, ParameterMap metrics) 
			throws ServerError;

    /**
     * Performs an Authorize
     *
     * @param parameters App_id etc for the authorize
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError;
    public AuthorizeResponse authorize(String serviceToken, String serviceId, ParameterMap parameters) 
    		throws ServerError;

    /**
     * Perform an Authorize using OAuth.
     *
     * @param params Parameters for the authorize
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError;
    public AuthorizeResponse oauth_authorize(String serviceToken, String serviceId, ParameterMap params) 
    		throws ServerError;

    /**
     * Report a set of metrics.  Note: report differs from the rest of these methods in that a serviceId is 
     * an argument.  The reason for this is that it does not accept a root level ParameterMap argument, which is
     * normally how the serviceId would be passed.  Instead, the root level ParameterMap is created by the 
     * implementation, and so the serviceId cannot be included in it. 
     *
     * @param transactions The metrics to be reported
     * @return Information about the success/failure of the operation
     * @throws ServerError Thrown if there is an error communicating with the 3Scale server
     */
    public ReportResponse report(String serviceId, ParameterMap... transactions) throws ServerError;
    public ReportResponse report(String serviceToken, String serviceId, ParameterMap... transactions) throws ServerError;

    /**
     * Get the URL of the 3scale server
     *
     * @return Server url
     */
    public String getHost();
}
