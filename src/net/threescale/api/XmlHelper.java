package net.threescale.api;

import java.io.*;

import javax.xml.xpath.*;

import org.xml.sax.*;

/**
 * Common methods used on XML.
 */
public class XmlHelper {

	/**
	 * Extract a String node from an XML Message
	 * @param xpath XPath object
	 * @param nodePath  XPath statement to locate the node
	 * @param xmlString Xml string object to extract the data from
	 * @return The requested data, or "" if not found.
	 */
	public static String extractNode(XPath xpath, String nodePath, String xmlString) {
		InputSource inputSource = new InputSource(new StringReader(xmlString));

		try {
			return xpath.evaluate(nodePath, inputSource);
		} catch (XPathExpressionException e) {
			return "";
		}
	}

}
