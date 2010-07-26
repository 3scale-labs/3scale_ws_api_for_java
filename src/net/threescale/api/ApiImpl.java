package net.threescale.api;

import java.util.*;
import java.util.logging.*;

/**
 * Concrete implementation of the Api. 
 */
public class ApiImpl implements Api {

	private Logger log = LogFactory.getLogger(this);

	private String host;
	private String provider_private_key;

	private HttpSender sender;
	
	/**
	 * Constructor.
	 * 
	 * @param url URL of the server to connect to. e.g. http://server.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 */
	ApiImpl(String url, String provider_private_key) {

		this.host = url;
		this.provider_private_key = provider_private_key;
		sender = new HttpSenderImpl();
	}

	/**
	 * Constructor.
	 * 
	 * @param url URL of the server to connect to. e.g. http://server.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @param sender to use for  communications with the server.
	 */
	ApiImpl(String url, String provider_private_key, HttpSender sender) {

		this.host = url;
		this.provider_private_key = provider_private_key;
		this.sender = sender;
	}

	/**
	 * Send start message to server for a user.
	 * 
	 * @param user_contract_key Contract key supplied by the User.
	 * @return On success. Contains transactionId, contract type etc.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public ApiStartResponse start(String user_contract_key) throws ApiException {
		return start(user_contract_key, null);

	}

	/**
	 * Send start message to server for a user with provisional metrics.
	 * 
	 * @param user_contract_key Contract key supplied by the User.
	 * @param metrics Provisional resources that this transaction may use. 
	 *                These are <key, value> pairs in the form <"name", "quantity">
	 * @return On success. Contains transactionId, contract type etc.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public ApiStartResponse start(String user_contract_key, Map<String, String> metrics)
			throws ApiException {

		log.info("transaction start for user_key: " + user_contract_key);
		return sender.sendPostToServer(createStartUrl(), buildPostData(user_contract_key, metrics));

	}

	/**
	 * Send a confirm message to the server on completion of the transaction. 
	 * This contains the actual resources used during the transaction and 
	 * will be allocated to the Users account.
	 * 
	 * @param transactionId The transactionId returned from the start operation.
	 * @param metrics Actual resources that this transaction used. 
	 *                These are <key, value> pairs in the form <"name", "quantity">
	 * @return 200 on success.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public int confirm(String transactionId, Map<String, String> metrics) throws ApiException {
		ApiStartResponse response = sender.sendPostToServer(createConfirmUrl(transactionId) , buildPostData(null, metrics));
		return response.getResponseCode();
	}

	
	/**
	 * Sends a cancel message to the server and aborts the transaction. 
	 * @param transactionId The transactionId returned from the start operation.
	 * @return 200 on success.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public int cancel(String transactionId) throws ApiException {
		return sender.sendDeleteToServer(createDeleteUrl(transactionId));
	}
	
	
	private String buildPostData(String user_contract_key,
			Map<String, String> metrics) {
		StringBuffer postData = new StringBuffer();

		if (user_contract_key != null) {
			postData.append("user_key=");
			postData.append(user_contract_key);
			postData.append("&");
		}
		
		postData.append("provider_key=");
		postData.append(provider_private_key);

		if (metrics != null) {
			for (Map.Entry<String, String> metric : metrics.entrySet()) {
				postData.append("&usage[");
				postData.append(metric.getKey());
				postData.append("]=");
				postData.append(metric.getValue());
			}
		}
		return postData.toString();
	}

	private String createConfirmUrl(String transactionId) {
		return host + "/transactions/" + transactionId + "/confirm.xml";
	}

	private String createDeleteUrl(String transactionId) {
		return host + "/transactions/" + transactionId + ".xml" + "?provider_key=" + provider_private_key;
	}

	private String createStartUrl() {
		return host + "/transactions.xml";
	}
}
