package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;


public class RemoteCacheImpl implements ApiCache {
    
    public RemoteCacheImpl(String path_to_config) {
    }

    public AuthorizeResponse getAuthorizeFor(String userKey) {
        return null;
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {

    }
}
