package net.threescale.api.v2;

/**
 * Version 2 of the 3Scale API.
 * <p/>
 * This version is not compatible with Version 1.
 * See http://www.3scale.net/support/api-service-management-v2-0/ for more details
 */
public interface Api2 {

    /**
     * Fetch the current statistics for an application.
     *
     * @param app_key  Optional Application Key (or null)
     * @param referrer Optional referrer ip address (or null)'
     * @return AuthorizeResponse containing the current usage metrics.
     * @throws ApiException if there is an error connection to the server
     */
    AuthorizeResponse authorize(String app_key, String referrer) throws ApiException;

    /**
     * Send a set of usage data to the server
     *
     * @param transactions Usage data to be recorded
     * @throws ApiException if there is an error connection to the server
     */
    void report(ApiTransaction[] transactions) throws ApiException;
}
