package net.threescale.api.v2;

import net.threescale.api.ApiHttpResponse;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 17:07:48
 */
public class HttpSenderImpl implements HttpSender {
    /**
     * Send a POST message.
     *
     * @param hostUrl  Url and parameters to send to the server.
     * @param postData Data to be POSTed.
     * @return Transaction data returned from the server.
     */
    public ApiHttpResponse sendPostToServer(String hostUrl, String postData) {
        return null;
    }

    /**
     * Send a Get message to the server
     *
     * @param hostUrlWithParameters
     * @return Response from Server for successful action
     */
    public String sendGetToServer(String hostUrlWithParameters) {
        return null;
    }
}
