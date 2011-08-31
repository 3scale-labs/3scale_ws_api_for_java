package net.threescale.api.v2;


/**
 * Interface used to send Http Requests to the server.
 */
public interface HttpSender {

    /**
     * Send a POST message.
     *
     * @param hostUrl  Url and parameters to send to the server.
     * @param postData Data to be POSTed.
     * @return Transaction data returned from the server.
     */
    public ApiHttpResponse sendPostToServer(String hostUrl, String postData);


    /**
     * Send a Get message to the server
     *
     * @param hostUrlWithParameters
     * @return Response from Server for successful action
     */
    public ApiHttpResponse sendGetToServer(String hostUrlWithParameters);
}
