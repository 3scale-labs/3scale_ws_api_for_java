package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.*;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;

import java.util.logging.Logger;

public abstract class CacheImplCommon implements ApiCache {

    private static final String authorize_prefix = "authorize";
    private static final String responseKey = "response";

    private Logger log = LogFactory.getLogger(this);
    
    private HttpSender sender;
    private String host_url;
    private String provider_key;

    // This is initialized by sub-class
    protected Cache data_cache;

    private long expirationTimeInMillis = 500L;

    public CacheImplCommon(String host_url, String provider_key, HttpSender sender) {
        this.sender = sender;
        this.host_url = host_url;
        this.provider_key = provider_key;
    }

    public AuthorizeResponse getAuthorizeFor(String app_key) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        return (AuthorizeResponse) data_cache.get(authorizeFqn, responseKey);
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        Node root = data_cache.getRoot();
        Node authorizeNode = data_cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }

        Long future = System.currentTimeMillis() + expirationTimeInMillis;
        authorizeNode.put(responseKey, authorizedResponse);
        authorizeNode.put("expiration", future);
    }

    public void close() {
        data_cache.stop();
        data_cache.destroy();
    }


    public void setExpirationInterval(long expirationTimeInMillis) {
        this.expirationTimeInMillis = expirationTimeInMillis;
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

    public void setSender(HttpSender sender) {
        this.sender = sender;
    }

    public void setHostUrl(String host_url) {
        this.host_url = host_url;
    }

    public void setProviderKey(String providerKey) {
        this.provider_key = providerKey;
    }
}
