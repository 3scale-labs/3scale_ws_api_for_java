package net.threescale.api;

import java.io.IOException;

/**
 * Interface used to send Http Requests to the server. 
 *
 */
public interface HttpSender {
	
	/**
	 * Send a POST message.
	 * @param hostUrl	Url and parameters to send to the server.
	 * @param postData	Data to be POSTed.
	 * @return	Transaction data returned from the server.
	 * @throws ApiException Error information.
	 */
	public ApiStartResponse sendPostToServer(String hostUrl, String postData) throws ApiException ;
	
	/**
	 * Send a DELETE message to the server.
	 * @param hostUrl Url and parameters to send to the server.
	 * @return Http Response code.
	 * @throws ApiException Error Information.
	 */
	public int sendDeleteToServer(String hostUrl) throws ApiException;


    /**
     * Send a Get message to the server
     * @param hostUrl
     * @return Response from Server for successful action
     * @throws ApiException Error information if request fails
     */
    public String sendGetToServer(String hostUrl) throws ApiException;
}
