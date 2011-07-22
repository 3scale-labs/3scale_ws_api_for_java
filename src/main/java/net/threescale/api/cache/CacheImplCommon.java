package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.Region;
import org.jboss.cache.config.EvictionRegionConfig;

import java.util.*;
import java.util.logging.Logger;


/**
 * Base class for cache classes.
 * @see net.threescale.api.cache.ApiCache
 */
public abstract class CacheImplCommon implements ApiCache {

    public static final String authorize_prefix = "/authorize";
    public static final String authorizeResponseKey = "/auth_response";

    public static final String responseKey = "/response";
    private String EXPIRATION_KEY = "expiration";

    private Logger log = LogFactory.getLogger(this);

    private HttpSender sender;
    private String host_url;
    private String provider_key;

    // This is initialized by sub-class
    private Cache data_cache;

    private long authorizeExpirationTimeInMillis = 500L;
    private long reportExpirationTimeInMillis = 500L;

    private long nextExpirationTime = new Date().getTime() + reportExpirationTimeInMillis;


    public CacheImplCommon(String host_url, String provider_key, HttpSender sender, Cache cache) {
        this.sender = sender;
        this.host_url = host_url;
        this.provider_key = provider_key;
        this.data_cache = cache;
        addEvictionPolicies(data_cache);
    }

    public AuthorizeResponse getAuthorizeFor(String app_id) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_id);
        return (AuthorizeResponse) data_cache.get(authorizeFqn, authorizeResponseKey);
    }


    public List<ApiTransaction> getTransactionFor(String app_id) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        List<ApiTransaction> results = new ArrayList<ApiTransaction>();

        Map data = data_cache.getData(reportFqn);
        Set<String> keys = data.keySet();

        for (String key : keys) {
            if (!key.equals(EXPIRATION_KEY)) {
                results.add((ApiTransaction) data.get(key));
            }
        }
        return results;
    }

    public ApiTransaction getTransactionFor(String app_id, String when) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        return (ApiTransaction) data_cache.get(reportFqn, when);
    }

    public Long getTransactionExpirationTimeFor(String app_id) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        return (Long) data_cache.get(reportFqn, EXPIRATION_KEY);
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        Node root = data_cache.getRoot();
        Node authorizeNode = data_cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }

        Long future = System.currentTimeMillis() + authorizeExpirationTimeInMillis;
        authorizeNode.put(authorizeResponseKey, authorizedResponse);
        authorizeNode.put(EXPIRATION_KEY, future);
    }

    public void close() {
        data_cache.stop();
        data_cache.destroy();
    }


    public void setAuthorizeExpirationInterval(long expirationTimeInMillis) {
        this.authorizeExpirationTimeInMillis = expirationTimeInMillis;
    }


    public void setReportExpirationInterval(long expirationTimeInMillis) {
        this.reportExpirationTimeInMillis = expirationTimeInMillis;
    }


    public void report(ApiTransaction[] transactions) throws ApiException {
        for (ApiTransaction transaction : transactions) {
            Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + transaction.getApp_id());

            Node reportNode = data_cache.getNode(reportFqn);
            if (reportNode == null) {
                reportNode = data_cache.getRoot().addChild(reportFqn);
            }

            reportNode.put(transaction.getTimestamp(), transaction);
            reportNode.put("expiration", nextExpirationTime);

            log.fine("Put transaction into cache as " + reportFqn + "/" + transaction.getTimestamp());
        }
    }


    public long getCurrentResponseExpirationTime() {
        return nextExpirationTime;
    }

    public void incrementCurrentResponseExpirationTime() {
        long currentTime = new Date().getTime();
        if (nextExpirationTime >= currentTime) {
            nextExpirationTime += reportExpirationTimeInMillis;
        }
    }


    /* Setup the Eviction policy for the response nodes
       Called after the cache has been created
     */
    private void addEvictionPolicies(Cache cache) {
        Fqn fqn = Fqn.fromString(responseKey);

        // Create a configuration for an LRUPolicy
        ECASTAlgorithmConfig config = new ECASTAlgorithmConfig(this, host_url, provider_key, sender);

        // Create an eviction region config
        EvictionRegionConfig erc = new EvictionRegionConfig(fqn, config);


        // Create the region and set the config
        Region region = cache.getRegion(fqn, true);
        region.setEvictionRegionConfig(erc);
    }

}
