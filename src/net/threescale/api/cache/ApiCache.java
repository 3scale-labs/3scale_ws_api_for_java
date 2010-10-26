package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 14:49:06
 */
public interface ApiCache {

    AuthorizeResponse getAuthorizeFor(String userKey);

    void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse);

    void close();

    void setExpirationInterval(long expirationTimeInMillis);
}
