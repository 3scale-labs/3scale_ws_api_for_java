package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 15:00:36
 */
public class NullCacheImpl implements ApiCache {

    private Logger log = LogFactory.getLogger(this);

    private HttpSender sender;
    private String host_url;
    private String provider_key;


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

    public void setProviderKey(String provider_key) {
        this.provider_key = provider_key;
    }
}