package net.threescale.api;

import net.threescale.api.v2.Api2;
import net.threescale.api.v2.Api2Impl;

/**
 * Factory class to create 3scale Api objects.
 */
public class ApiFactory {

	/**
	 * Creates a new Api object.
	 * @param url URL of the server to connect to. e.g. http://server.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @return A new Api object.
	 */
	public static net.threescale.api.Api createApi(String url, String provider_private_key) {
		return new ApiImpl(url, provider_private_key);
	}

	/**
	 * Creates a new Api object.
	 * @param url URL of the server to connect to. e.g. http://server.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @param sender The HttpSender object to be used for communication with the server.
	 * @return A new Api object.
	 */
	public static net.threescale.api.Api createApi(String url, String provider_private_key, HttpSender sender) {
		return new ApiImpl(url, provider_private_key, sender);
	}

	/**
	 * Creates a new Version 2 Api object.
	 * @param url URL of the server to connect to. e.g. http://su1.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @return A new Api object.
	 */
	public static net.threescale.api.v2.Api2 createV2Api(String url, String application_id, String provider_private_key) {
		return new Api2Impl(url, application_id, provider_private_key);
	}

	/**
	 * Creates a new Api object.
	 * @param url URL of the server to connect to. e.g. http://server.3scale.net.
	 * @param provider_private_key The Providers private key obtained from 3scale.
	 * @param sender The HttpSender object to be used for communication with the server.
	 * @return A new Api object.
	 */
	public static Api2 createV2Api(String url, String application_id, String provider_private_key,
                                                           net.threescale.api.v2.HttpSender sender) {
		return new Api2Impl(url, application_id, provider_private_key, sender);
	}
}
