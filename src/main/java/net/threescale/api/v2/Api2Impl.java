package net.threescale.api.v2;

import net.threescale.api.LogFactory;
import net.threescale.api.cache.ApiCache;
import net.threescale.api.cache.NullCacheImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Concrete implementation of the Version 2 API.
 */
public class Api2Impl implements Api2 {

    private Logger log = LogFactory.getLogger(this);

    private final String host_url;
    private final String provider_key;
    private final HttpSender sender;
    private ApiCache cache;

    /**
     * Normal constructor using standard HttpSender
     *
     * @param host_url     API authorization server URL
     * @param provider_key Private API Key from contract
     */
    public Api2Impl(String host_url,  String provider_key) {
        this(host_url, provider_key, new HttpSenderImpl());
    }

    /**
     * Constructor allowing injection of HttpSender (used for testing)
     *
     * @param host_url     API authorization server URL
     * @param provider_key Private API Key from contract
     * @param sender       HttpSender to use for communications.
     */
    public Api2Impl(String host_url, String provider_key, HttpSender sender) {
        this(host_url, provider_key, sender, new NullCacheImpl(host_url, provider_key, sender));
    }

    public Api2Impl(String host_url, String provider_key, ApiCache cache) {
        this(host_url, provider_key, new HttpSenderImpl(), cache);
    }

    public Api2Impl(String host_url, String provider_key, HttpSender sender, ApiCache cache) {
        this.host_url = host_url;
        this.provider_key = provider_key;
        this.sender = sender;
        this.cache = cache;
    }

    /**
     * Fetch the current statistics for an application, using an app_id.
     *
     * @param app_id  Application id (or null)
     * @param app_key  Optional Application Key (or null)
     * @param referrer Optional referrer ip address (or null)'
     * @return AuthorizeResponse containing the current usage metrics.
     * @throws ApiException if there is an error connection to the server
     */
    public AuthorizeResponse authorize(String app_id, String app_key, String referrer, HashMap<String, String> usage_metrics) throws ApiException {

        AuthorizeResponse cached_response = cache.getAuthorizeFor(app_id, app_key, referrer, null, usage_metrics);
        if (cached_response == null) {
            String url = formatGetUrl(app_id, app_key, referrer, null, usage_metrics);
            log.info("Sending GET to sever with url: " + url);

            ApiHttpResponse response = sender.sendGetToServer(url);

            log.info("response code was: " + response.getResponseCode());

            if (response.getResponseCode() == 200 || response.getResponseCode() == 409) {
                AuthorizeResponse authorizedResponse = new AuthorizeResponse(response.getResponseText());
                cache.addAuthorizedResponse(app_id, authorizedResponse, app_key, referrer, null, usage_metrics);
                return authorizedResponse;
            } else if (response.getResponseCode() == 403 || response.getResponseCode() == 404) {
                throw new ApiException(response.getResponseText());
            } else {
                throw ApiUtil.createExceptionForUnexpectedResponse(log, response);
            }
        } else {
            return cached_response;
        }
    }


    /**
     * Fetch the current statistics for an application, using an app_id.
     * @param user_key User_key or null, one of app_id, or user_key must be set.
     * @param referrer Optional referrer ip address (or null)'
     * @return AuthorizeResponse containing the current usage metrics.
     * @throws ApiException if there is an error connection to the server
     */
    public AuthorizeResponse authorizeWithUserKey(String user_key, String referrer, HashMap<String, String> usage_metrics) throws ApiException {

        String app_key = null; // Should always be null
        
        AuthorizeResponse cached_response = cache.getAuthorizeFor(null, app_key, referrer, user_key, usage_metrics);
        if (cached_response == null) {
            String url = formatGetUrl(null, app_key, referrer, user_key, usage_metrics);
            log.info("Sending GET to sever with url: " + url);

            ApiHttpResponse response = sender.sendGetToServer(url);

            log.info("response code was: " + response.getResponseCode());

            if (response.getResponseCode() == 200 || response.getResponseCode() == 409) {
                AuthorizeResponse authorizedResponse = new AuthorizeResponse(response.getResponseText());
                cache.addAuthorizedResponse(null, authorizedResponse, app_key, referrer, null, usage_metrics);
                return authorizedResponse;
            } else if (response.getResponseCode() == 403 || response.getResponseCode() == 404) {
                throw new ApiException(response.getResponseText());
            } else {
                throw ApiUtil.createExceptionForUnexpectedResponse(log, response);
            }
        } else {
            return cached_response;
        }
    }


    @Override
    public AuthorizeResponse authorize(String app_id, String app_key, String referrer) throws ApiException {
        return authorize(app_id, app_key, referrer, null);
    }

    /**
     * Send a set of usage data to the server
     *
     * @param transactions Usage data to be recorded
     * @throws ApiException if there is an error connection to the server
     */
    public void report(ApiTransaction[] transactions) throws ApiException {

        cache.report(transactions);
    }

    @Override
    public String getServerUrl() {
        return host_url;
    }


// Private Methods

    private String formatGetUrl(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        StringBuffer url = new StringBuffer();

        url.append(host_url)
                .append("/transactions/authorize.xml")
                .append("?provider_key=")
                .append(provider_key);
        if (app_id != null) {
            url.append("&app_id=").append(app_id);

        }
        if (app_key != null) {
            url.append("&app_key=")
                    .append(app_key);
        }


        if (user_key != null) {
            url.append("&user_key=")
                    .append(user_key);
        }


        if (referrer != null) {
            url.append("&referrer=")
                    .append(referrer);
        }

        if (usage != null) {
            Set<Map.Entry<String,String>> entries = usage.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                url.append("&usage[")
                   .append(entry.getKey())
                   .append("]=")
                   .append(entry.getValue());
            }
        }

        return url.toString();
    }


}

