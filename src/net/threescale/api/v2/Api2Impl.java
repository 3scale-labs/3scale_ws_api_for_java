package net.threescale.api.v2;

import net.threescale.api.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


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

    public ApiResponse authorize(String app_key) throws ApiException {
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
        log.info("response code was: " + response.getResponseCode());
        if (response.getResponseCode() == 200) {
            return new ApiResponse(response.getResponseText());
        } else if (response.getResponseCode() == 403) {
            throw new ApiException(response.getResponseText());
        } else {
            throw createExceptionForUnexpectedResponse(response);
        }
    }

    private ApiException createExceptionForUnexpectedResponse(ApiHttpResponse response) {
        return new ApiException("Server gave unexpected response",
                               "Response Code was \"" + response.getResponseCode()+ "\"" +
                               "with text \"" + response.getResponseText() +"\"");
    }

    public void report(ApiTransaction[] transactions) throws ApiException {

        StringBuffer post_data = formatPostData(transactions);

        ApiHttpResponse response = sender.sendPostToServer(host_url, post_data.toString());

        if (response.getResponseCode() == 202) {
            return;
        } else if (response.getResponseCode() == 403) {
            throw new ApiException(response.getResponseText());
        } else {
            throw createExceptionForUnexpectedResponse(response);
        }
    }

    private String formatTransactionDataForPost(int index, ApiTransaction transaction) {
        StringBuffer data = new StringBuffer();
        String prefix = "transactions[" + index + "]";

        data.append(prefix);
        data.append("[app_id]=").append(transaction.getApp_id());
        data.append(formatMetrics(prefix, transaction.getMetrics()));
        data.append("&").append(prefix);
        data.append("[timestamp]=").append(urlEncodeField(transaction.getTimestamp()));

        return data.toString();
     }

    private String urlEncodeField(String field_to_encode) {
        try {
            return URLEncoder.encode(field_to_encode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return field_to_encode;
        }
    }

    private String formatMetrics(String prefix, HashMap<String, String> metrics) {
        StringBuffer data = new StringBuffer();

        Set<Map.Entry<String,String>> entries = metrics.entrySet();

        for(Map.Entry<String,String> entry : entries) {
            data.append("&").append(prefix).append("[usage]");
            data.append("[" + entry.getKey() + "]=" + entry.getValue());
        }
        return data.toString();
    }

    /** This is only used for testing **/
    void setHttpSender(HttpSender sender) {
        this.sender = sender;
    }

    public StringBuffer formatPostData(ApiTransaction[] transactions) {
        StringBuffer post_data = new StringBuffer();

        for (int index = 0; index < transactions.length; index++) {
            if (index != 0) {
                post_data.append("&");
            }
            post_data.append(formatTransactionDataForPost(index, transactions[index]));
        }
        return post_data;
    }
}

