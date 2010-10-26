package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;


public class LocalCacheImpl implements ApiCache {
    
    public AuthorizeResponse getAuthorizeFor(String userKey) {
        return null;
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {

    }
}
