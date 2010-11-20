package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 15:00:36
 */
public class NullCacheImpl implements ApiCache {

    /**
     * Always returns Null so the data is loaded from the server
     * 
     * @param userKey
     * @return  null
     */
    public AuthorizeResponse getAuthorizeFor(String userKey) {
        return null;
    }

    public void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse) {
        // Does nothing
    }

    public void close() {
        // Does Nothing
    }

    public void setExpirationInterval(long expirationTimeInMillis) {
        // Does Nothings
    }
}
