package net.threescale.api;

import java.util.logging.*;

import javax.xml.xpath.*;

/**
 * Contains the transaction information returned from a successful start operation.
 */
public class ApiStartResponse {

	private Logger log = LogFactory.getLogger(this);

	private String transactionId = "";
	private String contractName = "";
	private String providerVerificationKey = "";
	private int responseCode;

	/**
	 * Constructor using the XML returned from a start operation.
	 * @param xmlString XML returned from the server.
	 * @param responseCode Http Response code from the Http response.
	 */
	public ApiStartResponse(String xmlString, int responseCode) throws ApiException {
		log.info("Response code was: " + responseCode);
		
		if (responseCode == 200) {
			this.responseCode = responseCode;
	
			if (xmlString != null && xmlString.trim().length() != 0) {

                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xpath = xPathFactory.newXPath();

				log.info("Extracting transaction info");
				transactionId = XmlHelper.extractNode(xpath, "//transaction/id", xmlString);
				contractName = XmlHelper.extractNode(xpath, "//transaction/contract_name",
						xmlString);
				providerVerificationKey = XmlHelper.extractNode(xpath,
						"/transaction/provider_verification_key", xmlString);
				log.info("tid: " + transactionId);
				log.info("contractName: " + contractName);
			} else {
				log.info("Xml string was empty");
				throw new ApiException(500, "provider.other", "Xml response was empty");
				
			}
		}
		else {
			log.info("Throwing ApiException");
			log.info("responseCode: " + responseCode);
			log.info("xml: " + xmlString);
			throw new ApiException(responseCode, xmlString);
		}
		
	}


	/**
	 * Constructor to build a specific response. 
	 * 
	 * @param transactionId Transaction Id.
	 * @param contractName Contract type.
	 * @param providerVerificationKey Verification key.
	 * @param responseCode Http Response code.
	 */
	public ApiStartResponse(String transactionId, String contractName,
			String providerVerificationKey, int responseCode) {
		this.transactionId = transactionId;
		this.contractName = contractName;
		this.providerVerificationKey = providerVerificationKey;
		this.responseCode = responseCode;
	}

	/**
	 * Get the transaction Id.
	 * @return Transaction Id
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Get the type of Contract for this User.
	 * @return Contract type
	 */
	public String getContractName() {
		return contractName;
	}

	/**
	 * Get the Providers Verification Key.
	 * @return Providers verification key
	 */
	public String getProviderVerificationKey() {
		return providerVerificationKey;
	}

	/**
	 * Get the Http Response code for this request. 
	 * This is the standard Http Response, see the 3scale Management 
	 * Api Specification for exact codes. 
	 * @return Response code
	 */
	public int getResponseCode() {
		return responseCode;
	}

}
