package net.threescale.api;

import javax.xml.xpath.*;

/**
 * Contains the reason information returned when an Api call fails.
 * 
 */
public class ApiException extends Exception {

	private static final long serialVersionUID = 2909145065587168842L;

	private int responseCode;
	private String errorId;
	private String errorMessage;
	
    public ApiException(int responseCode, String errorId, String errorMessage) {

        this.responseCode = responseCode;
        this.errorId = errorId;
        this.errorMessage = errorMessage;
    }

    public ApiException(int responseCode, String xmlMessage) {
		this.responseCode = responseCode;

		try {
			// XPathFactory xPathFactory = XPathFactory.newInstance();
			XPathFactory xPathFactory = new org.apache.xpath.jaxp.XPathFactoryImpl();
			XPath xpath = xPathFactory.newXPath();

            if (xmlMessage != null && xmlMessage.length() > 0) {
			    errorId = XmlHelper.extractNode(xpath, "//@id", xmlMessage);
			    errorMessage = XmlHelper.extractNode(xpath, "/error", xmlMessage);
            } else {
                errorId = "provider.other";
                this.responseCode = 500;
                errorMessage = "xml error parsing response from server";
            }

		} catch (Exception e) {
			errorId = "500";
			errorMessage = "xml error parsing response from server";
			e.printStackTrace();
		}
	}


	/**
	 * The response code from the underlaying Http call.
	 * @return The response code.
	 */
	public int getResponseCode() {
		return responseCode;
	}
	
	/**
	 * Get the error id.
	 * @return The error Id returned from the server.
	 */
	public String getErrorId() {
		return errorId;
	}
	
	/**
	 * Get the human readable form of the message.
	 * @return The message.
	 */
	public String getMessage() {
		return errorMessage;
	}
	
	public String toString() {
		return "[code:" + responseCode + " id:" + getErrorId() + ", message: " + getMessage() + "]";
	}
}
