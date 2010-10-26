package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;

public abstract class CacheImplCommon {

    private static final String authorize_prefix = "authorize";
    private static final String responseKey = "response";

    // This is initialized by sub-class
    protected Cache cache;

    public AuthorizeResponse getAuthorizeFor(String app_key) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        return (AuthorizeResponse)cache.get(authorizeFqn, responseKey);
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {
        Fqn<String> authorizeFqn = Fqn.fromString(authorize_prefix + "/" + app_key);
        Node root = cache.getRoot();
        Node authorizeNode = cache.getNode(authorizeFqn);
        if (authorizeNode == null) {
            authorizeNode = root.addChild(authorizeFqn);
        }
        authorizeNode.put(responseKey, authorizedResponse);
    }

}
