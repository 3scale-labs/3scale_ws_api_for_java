package net.threescale.api.cache;

import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;

import java.util.HashMap;
import java.util.List;


/**
 * Interface for ApiCaches.
 */
public interface ApiCache {

    /**
     * Get authorize for a user key.
     * @param app_id
     * @param app_key
     * @param referrer
     * @param user_key
     *@param usage  @return
     */
    AuthorizeResponse getAuthorizeFor(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage);

    /**
     * Add authorized response to the cache.
     * @param app_id App Id
     * @param authorizedResponse Response data to add.
     * @param app_key
     * @param referrer
     * @param user_key
     * @param usage
     */
    void addAuthorizedResponse(String app_id, AuthorizeResponse authorizedResponse, String app_key, String referrer, String user_key, HashMap<String, String> usage);

    /**
     * Close the cache.
     */
    void close();

    /**
     * Set the time that authorize data will remain in the cache.
     * @param expirationTimeInMillis Period in milliseconds.
     */
    void setAuthorizeExpirationInterval(long expirationTimeInMillis);

    /**
     * Set the maximum time that report statistics will remain in the cache before being sent to the server.
     * @param expirationTimeInMillis
     */
    void setReportExpirationInterval(long expirationTimeInMillis);

    /**
     * Store report statistics in the cache.
     * @param transactions
     * @throws ApiException
     */
    void report(ApiTransaction[] transactions) throws ApiException;

    /**
     * `get the current set of transactions for a specific app it.
     * @param app_id App id
     * @return  current transactions.
     */
    List<ApiTransaction> getTransactionFor(String app_id);

    /**
     * Get a specific transaction (mainly for testing).
     * @param app_id  App Id
     * @param when   Timestamp
     * @return  Transaction or null it one does not exist.
     */
    ApiTransaction getTransactionFor(String app_id, String when);

    /**
     * Get the time that the next set of transactions will expire for a given app id.
     * @param app_id
     * @return
     */
    Long getTransactionExpirationTimeFor(String app_id);

    /**
     * get the time the current set of responses will exipre (i.e. be written to server).
     * @return
     */
    long getCurrentResponseExpirationTime();

    /**
     * Increment the response expiration time to the next period.
     */
    void incrementCurrentResponseExpirationTime();
}
