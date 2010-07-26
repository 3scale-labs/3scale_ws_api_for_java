package net.threescale.api;

import java.util.*;

/**
 * Public interface for the 3scale API.
 * 
 */
public interface Api {

	/**
	 * Send start message to server for a user.
	 * 
	 * @param user_contract_key Contract key supplied by the User.
	 * @return On success. Contains transactionId, contract type etc.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public ApiStartResponse start(String user_contract_key) throws ApiException;

	/**
	 * Send start message to server for a user with provisional metrics.
	 * 
	 * @param user_contract_key Contract key supplied by the User.
	 * @param metrics Provisional resources that this transaction may use. 
	 *                These are <key, value> pairs in the form <"name", "quantity">
	 * @return On success. Contains transactionId, contract type etc.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public ApiStartResponse start(String user_contract_key, Map<String, String> metrics) throws ApiException;
	
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
	public int confirm(String transactionId, Map<String, String> metrics) throws ApiException;

	/**
	 * Sends a cancel message to the server and aborts the transaction. 
	 * @param transactionId The transactionId returned from the start operation.
	 * @return 200 on success.
	 * @throws ApiException On error. Contains the cause information for the error.
	 */
	public int cancel(String transactionId) throws ApiException;
	
}
