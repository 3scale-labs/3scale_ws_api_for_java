package net.threescale.api.cache;

import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;

import java.util.List;


public interface ApiCache {

    AuthorizeResponse getAuthorizeFor(String userKey);

    void addAuthorizedResponse(String app_key, AuthorizeResponse authorizedResponse);

    void close();

    void setAuthorizeExpirationInterval(long expirationTimeInMillis);

    void setReportExpirationInterval(long expirationTimeInMillis);

    void report(ApiTransaction[] transactions) throws ApiException;

    List<ApiTransaction> getTransactionFor(String app_id);
    
    ApiTransaction getTransactionFor(String app_id, String when);

    Long getTransactionExpirationTimeFor(String app_id);

    long getCurrentResponseExpirationTime();

    void incrementCurrentResponseExpirationTime();
}
