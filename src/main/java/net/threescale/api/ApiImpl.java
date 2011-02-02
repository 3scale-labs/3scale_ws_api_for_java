package net.threescale.api;

import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		ApiHttpResponse response = sender.sendPostToServer(createStartUrl(), buildPostData(user_contract_key, metrics));
        return new ApiStartResponse(response.getResponseText(), response.getResponseCode());

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
		ApiHttpResponse response = sender.sendPostToServer(createConfirmUrl(transactionId) , buildPostData(null, metrics));
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

    /**
     * Sends an authorize message to the server and get the response
     *
     * @param user_contract_key
     * @return The respone from the authorize request
     * @throws ApiException
     *          if the request fails. Contains the cause information for the error
     */
    public ApiAuthorizeResponse authorize(String user_contract_key) throws ApiException {
        return buildAuthorizeResponse(sender.sendGetToServer(createAuthorizeUrl(user_contract_key)));
    }

    /**
     * Send a set of Usage metrics to the server
     * @param batchMetrics  Array of user_key / metrics
     * @return  Code 201 if successful,
     * @throws ApiException Thrown if any error.  Use getErrors() to investigate the problems.
     */
    public int batch(ApiBatchMetric[] batchMetrics) throws ApiException {
        log.info("batch start: for " + batchMetrics.length + " transactions");
        ApiHttpResponse response = sender.sendPostToServer(createBatchUrl(), buildBatchData(batchMetrics));
        if (response.getResponseCode() != 201) {
            throw new ApiException(response.getResponseCode(), response.getResponseText());
        }
        return response.getResponseCode();
    }

    private ApiAuthorizeResponse buildAuthorizeResponse(String responseFromServer) throws ApiException {
        return new ApiAuthorizeResponse(responseFromServer); 
    }


    /** This method is only public for testing, do not call in your code */
    public String buildBatchData(ApiBatchMetric[] batchMetrics) {
        StringBuffer response = new StringBuffer();
        response.append("provider_key=");
        response.append(provider_private_key);
        int index = 0;
        for (ApiBatchMetric metric : batchMetrics) {
            response.append("&").append(buildMetricItem(metric, index++));
        }
        return response.toString().replaceAll(" ", "%20");
    }

    private String buildMetricItem(ApiBatchMetric metric,int index) {
        String prefix = "transactions[" + index + "]" ;
        StringBuffer b = new StringBuffer();
        b.append(prefix).append("[user_key]=").append(metric.getUser_key());

        Set<String> keySet = metric.getMetrics().keySet();
        for (String key : keySet) {
            b.append("&").append(buildIndividualMetric(prefix, key, metric.getMetrics()));
        }
        String timeStamp = buildTimestampString(metric.getTransactionTime());
        if (timeStamp != null) {
            b.append("&").append(prefix).append("[timestamp]=").append(timeStamp);
        }

        return b.toString();
    }

    private String buildTimestampString(Date transactionTime) {
        if (transactionTime != null) {
            String dateString = ApiUtil.getDataFormatter().format(transactionTime);
            String tzString = new SimpleDateFormat("Z").format(transactionTime);
            return dateString + " " + (tzString.substring(0,3) + ":" + tzString.substring(3));
        } else {
            return null;
        }
    }

    private String buildIndividualMetric(String prefix, String key, Map<String, String> metrics) {
        StringBuffer b = new StringBuffer();
        b.append(prefix);
        b.append("[usage][").append(key).append("]=");
        b.append(metrics.get(key));
        return b.toString();
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

    private String createBatchUrl() {
        return host + "/transactions.xml";
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

    private String createAuthorizeUrl(String user_key) {
        return host + "/transactions/authorize.xml?user_key=" + user_key + "&provider_key=" + provider_private_key;
    }
}
