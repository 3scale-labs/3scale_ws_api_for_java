package net.threescale.api;

/**
 * Factory class to create 3scale Api objects.
 */
public class ApiFactory {

	/**
	 * Creates a new Api object.
	 * @param url URL of the server to connect to. e.g. http://beta.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @return A new Api object.
	 */
	public static Api createApi(String url, String provider_private_key) {
		return new ApiImpl(url, provider_private_key);
	}

	/**
	 * Creates a new Api object.
	 * @param url URL of the server to connect to. e.g. http://beta.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @param sender The HttpSender object to be used for communication with the server.
	 * @return A new Api object.
	 */
	public static Api createApi(String url, String provider_private_key, HttpSender sender) {
		return new ApiImpl(url, provider_private_key, sender);
	}
}
