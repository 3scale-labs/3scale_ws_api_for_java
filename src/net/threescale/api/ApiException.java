package net.threescale.api;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * Contains the reason information returned when an Api call fails.
 * 
 */
public class ApiException extends Exception {

	private int responseCode;
	private String errorId;
	private String errorMessage;
	
	public ApiException(int responseCode, String xmlMessage) {
		this.responseCode = responseCode;

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();

		errorId = XmlHelper.extractNode(xpath, "//@id", xmlMessage);
		errorMessage = XmlHelper.extractNode(xpath, "/error", xmlMessage);
	}

	public ApiException(int responseCode, String errorId, String message) {
		this.responseCode = responseCode;
		this.errorId = errorId;
		this.errorMessage = message;
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
