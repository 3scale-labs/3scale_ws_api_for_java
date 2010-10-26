package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;
import org.jboss.cache.*;


public class LocalCacheImpl implements ApiCache {

    private static final String authorize_prefix = "authorize";
    private static final String responseKey = "response";

    private Cache cache;

    public LocalCacheImpl() {

        CacheFactory factory = new DefaultCacheFactory();
        cache = factory.createCache();
    }

    public AuthorizeResponse getAuthorizeFor(String app_key) {
        Fqn authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        return (AuthorizeResponse)cache.get(authorizeFqn, responseKey);
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {
        Fqn authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        Node root = cache.getRoot();
        Node authorizeNode = cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }
        authorizeNode.put(responseKey, authorizedResponse);
    }
}
