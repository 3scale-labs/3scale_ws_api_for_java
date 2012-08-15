package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.ApplicationResponse;
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
    public static final String oauth_authorize_prefix = "/oauth_authorize";
    public static final String authorizeResponseKey = "/auth_response";
    public static final String applicationResponseKey = "/app_response";
    public static final String application_prefix = "/application";
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
    private long applicationExpirationTimeInMillis = 60000L;
    private long nextExpirationTime = new Date().getTime() + reportExpirationTimeInMillis;


    public CacheImplCommon(String host_url, String provider_key, HttpSender sender, Cache cache) {
        this.sender = sender;
        this.host_url = host_url;
        this.provider_key = provider_key;
        this.data_cache = cache;
        addEvictionPolicies(data_cache);
    }

    public AuthorizeResponse getAuthorizeFor(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        Fqn<String> authorizeFqn = authorizeKeyFrom(app_id, app_key, referrer, user_key, usage);

        return (AuthorizeResponse) data_cache.get(authorizeFqn, authorizeResponseKey);
    }

    public AuthorizeResponse getOAuthAuthorizeFor(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        Fqn<String> authorizeFqn = oauth_authorizeKeyFrom(app_id, app_key, referrer, user_key, usage);

        return (AuthorizeResponse) data_cache.get(authorizeFqn, authorizeResponseKey);
    }

    public List<ApiTransaction> getTransactionFor(String app_id) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        List<ApiTransaction> results = new ArrayList<ApiTransaction>();

        Map data = data_cache.getData(reportFqn);
        Set<String> keys = data.keySet();

        for (String key : keys) {
            if (!key.equals(EXPIRATION_KEY)) {
                List<ApiTransaction> currentData = (List<ApiTransaction>) data.get(key);
                for (ApiTransaction transaction : currentData) {
                    results.add(transaction);
                }
            }
        }
        return results;
    }

    public ApiTransaction[] getTransactionFor(String app_id, String when) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        List<ApiTransaction> data = (List<ApiTransaction>)data_cache.get(reportFqn, when);
        if (data == null || data.size() == 0)
            return null;
        else
            return  data.toArray(new ApiTransaction[0]);
    }

    public Long getTransactionExpirationTimeFor(String app_id) {
        Fqn<String> reportFqn = Fqn.fromString(responseKey + "/" + app_id);
        return (Long) data_cache.get(reportFqn, EXPIRATION_KEY);
    }

    public void addAuthorizedResponse(String app_id, AuthorizeResponse authorizedResponse, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        Fqn<String> authorizeFqn = authorizeKeyFrom(app_id, app_key, referrer, user_key, usage);
        Node root = data_cache.getRoot();
        Node authorizeNode = data_cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }

        Long future = System.currentTimeMillis() + authorizeExpirationTimeInMillis;
        authorizeNode.put(authorizeResponseKey, authorizedResponse);
        authorizeNode.put(EXPIRATION_KEY, future);
    }

    public void addOAuthAuthorizedResponse(String app_id, AuthorizeResponse authorizedResponse, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        Fqn<String> authorizeFqn = oauth_authorizeKeyFrom(app_id, app_key, referrer, user_key, usage);
        Node root = data_cache.getRoot();
        Node authorizeNode = data_cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }

        Long future = System.currentTimeMillis() + authorizeExpirationTimeInMillis;
        authorizeNode.put(authorizeResponseKey, authorizedResponse);
        authorizeNode.put(EXPIRATION_KEY, future);
    }

    public void addApplicationFor(ApplicationResponse app_response, String application_id, String user_key, String app_id){
    	Fqn<String> authorizeFqn = applicationKeyFrom(application_id,user_key, app_id);
        Node root = data_cache.getRoot();
        Node authorizeNode = data_cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }

        Long future = System.currentTimeMillis() + applicationExpirationTimeInMillis;
        authorizeNode.put(applicationResponseKey, app_response);
        authorizeNode.put(EXPIRATION_KEY, future);
    }
    
    public ApplicationResponse getApplicationFor(String application_id, String user_key, String app_id){
    Fqn<String> applicationFqn = applicationKeyFrom(application_id,user_key, app_id);

        return (ApplicationResponse) data_cache.get(applicationFqn, applicationResponseKey);
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

            List<ApiTransaction> currentList = (List<ApiTransaction>)reportNode.get(transaction.getTimestamp());
            if (currentList == null) {
                currentList = new ArrayList<ApiTransaction>();
            }
            currentList.add(transaction);
            reportNode.put(transaction.getTimestamp(), currentList);
            reportNode.put(EXPIRATION_KEY, nextExpirationTime);

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

    private Fqn<String> authorizeKeyFrom(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        String usage_as_string;

        app_key = valueOrNone(app_key);
        user_key = valueOrNone(user_key);
        referrer = valueOrNone(referrer);

        if (usage == null) {
            usage_as_string = "none";
        } else {
            StringBuffer sb = new StringBuffer();
            Set<String> keys = usage.keySet();
            for (String key : keys) {
                sb.append("&");
                sb.append(key);
            }
            usage_as_string = sb.toString();
        }

        return Fqn.fromString(authorize_prefix + "/" + app_id + "/" + app_key + "/"+ user_key + "/" + referrer + "/" + usage_as_string);
    }

    private Fqn<String> oauth_authorizeKeyFrom(String app_id, String app_key, String referrer, String user_key, HashMap<String, String> usage) {
        String usage_as_string;

        app_key = valueOrNone(app_key);
        user_key = valueOrNone(user_key);
        referrer = valueOrNone(referrer);

        if (usage == null) {
            usage_as_string = "none";
        } else {
            StringBuffer sb = new StringBuffer();
            Set<String> keys = usage.keySet();
            for (String key : keys) {
                sb.append("&");
                sb.append(key);
            }
            usage_as_string = sb.toString();
        }

        return Fqn.fromString(oauth_authorize_prefix + "/" + app_id + "/" + app_key + "/"+ user_key + "/" + referrer + "/" + usage_as_string);
    }

    private Fqn<String> applicationKeyFrom(String application_id, String user_key, String app_id) {
        application_id = valueOrNone(application_id);
        user_key = valueOrNone(user_key);
        app_id = valueOrNone(app_id);
        return Fqn.fromString(oauth_authorize_prefix + "/" + app_id + "/" + application_id + "/"+ user_key);
    }

    private String valueOrNone(String app_key) {
        if (app_key == null) {
            app_key = "none";
        }
        return app_key;
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
