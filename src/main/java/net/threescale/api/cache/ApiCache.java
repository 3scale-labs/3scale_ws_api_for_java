package net.threescale.api.cache;

import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;

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

    void setAuthorizeExpirationInterval(long expirationTimeInMillis);

    void setReportExpirationInterval(long expirationTimeInMillis);

    void report(ApiTransaction[] transactions) throws ApiException;
}
