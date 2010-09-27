package net.threescale.api.v2;

import net.threescale.api.LogFactory;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:54:49
 */
public class Api2Impl implements Api2 {

    private Logger log = LogFactory.getLogger(this);

    private final String host_url;
    private final String app_id;
    private final String provider_key;
    private HttpSender sender;

    public Api2Impl(String host_url, String app_id, String provider_key) {
        this.host_url = host_url;
        this.app_id = app_id;
        this.provider_key = provider_key;
    }

    public ApiResponse authorize(String app_key) {
        StringBuffer url = new StringBuffer();

        url.append(host_url)
           .append("/transactions/authorize.xml")
           .append("?app_id=").append(app_id)
           .append("&provider_key=")
           .append(provider_key);
        if (app_key != null) {
            url.append("&app_key=")
               .append(app_key);
        }

        String urlAsString = url.toString();
        log.info("Sending GET to sever with url: " + urlAsString);
        ApiHttpResponse response = sender.sendGetToServer(urlAsString);
        if (response.getResponseCode() == 200) {
            return new ApiResponse(response.getResponseText());
        }
        return null;
    }

    public void report(ApiTransaction[] transactions) throws ApiException {

    }

    /** This is only used for testing **/
    void setHttpSender(HttpSender sender) {
        this.sender = sender;
    }
}
