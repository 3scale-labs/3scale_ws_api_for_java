package net.threescale.api;

import net.threescale.api.cache.ApiCache;
import net.threescale.api.cache.LocalCacheImpl;
import net.threescale.api.cache.RemoteCacheImpl;
import net.threescale.api.v2.*;

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
	public static net.threescale.api.Api createApi(String url, String provider_private_key, net.threescale.api.HttpSender sender) {
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
	public static net.threescale.api.v2.Api2 createV2Api(String url, String application_id, String provider_private_key,
                                                           net.threescale.api.v2.HttpSender sender) {
		return new Api2Impl(url, application_id, provider_private_key, sender);
	}

    public static Api2 createV2ApiWithCache(String url, String application_id, String provider_private_key,
                                                           net.threescale.api.v2.HttpSender sender, ApiCache cache) {
        return new Api2Impl(url, application_id, provider_private_key, sender, cache);
    }

    public static Api2 createV2ApiWithLocalCache(String url, String application_id, String provider_private_key,
                                                           net.threescale.api.v2.HttpSender sender) {
        return new Api2Impl(url, application_id, provider_private_key, sender, new LocalCacheImpl());
    }
    
    public static Api2 createV2ApiWithRemoteCache(String url, String application_id, String provider_private_key,
                                                           String path_to_config) {
        return new Api2Impl(url, application_id, provider_private_key, new RemoteCacheImpl(path_to_config));
    }
}
