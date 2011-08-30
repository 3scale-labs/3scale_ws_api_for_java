package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Null Cache that sends everything straight to the server.
 * @see net.threescale.api.cache.ApiCache
 */
public class NullCacheImpl implements ApiCache {

    private Logger log = LogFactory.getLogger(this);

    private HttpSender sender;
    private String host_url;
    private String provider_key;

    public NullCacheImpl(String host_url, String provider_key, HttpSender sender) {
        this.host_url = host_url;
        this.provider_key = provider_key;
        this.sender = sender;
    }


    /**
     * Always returns Null so the data is loaded from the server
     *
     * @param userKey
     * @param app_key
     * @param referrer
     * @param user_key
     * @param usage @return null
     */
    public AuthorizeResponse getAuthorizeFor(String userKey, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        return null;
    }

    public void addAuthorizedResponse(String app_id, AuthorizeResponse authorizedResponse, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        // Does nothing
    }

    public void close() {
        // Does Nothing
    }

    public void setAuthorizeExpirationInterval(long expirationTimeInMillis) {
        // Does Nothings
    }

    public void setReportExpirationInterval(long expirationTimeInMillis) {
        // Does Nothings
    }

    public void report(ApiTransaction[] transactions) throws ApiException {
        String post_data = ApiUtil.formatPostData(provider_key, transactions);

        ApiHttpResponse response = sender.sendPostToServer(host_url, post_data);

        if (response.getResponseCode() == 202) {
            return;
        } else if (response.getResponseCode() == 403) {
            throw new ApiException(response.getResponseText());
        } else {
            throw ApiUtil.createExceptionForUnexpectedResponse(log, response);
        }
    }

    public List<ApiTransaction> getTransactionFor(String app_id) {
        return new ArrayList<ApiTransaction>();
    }

    public ApiTransaction getTransactionFor(String app_id, String when) {
        return null;
    }

    public Long getTransactionExpirationTimeFor(String app_id) {
        return null;
    }

    public long getCurrentResponseExpirationTime() {
        return 0;
    }

    public void incrementCurrentResponseExpirationTime() {
        // Does nothing
    }
}
