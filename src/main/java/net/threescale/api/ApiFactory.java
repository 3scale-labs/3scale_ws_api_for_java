package net.threescale.api;

import net.threescale.api.cache.ApiCache;
import net.threescale.api.cache.ConfiguredCacheImpl;
import net.threescale.api.cache.DefaultCacheImpl;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.Api2Impl;
import net.threescale.api.v2.HttpSender;
import net.threescale.api.v2.HttpSenderImpl;

/**
 * Factory class to create 3scale Api objects.
 */
public class ApiFactory {
    /** Default URL of 3Scale Provider {@value}*/
    public static String DEFAULT_3SCALE_PROVIDER_API_URL = "http://su1.3scale.net";

    /**
     * Creates a new Version 2 Api object.
     *
     * @param url                  URL of the server to connect to. e.g. http://su1.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @return A new Api object.
     */
    public static net.threescale.api.v2.Api2 createV2Api(String url, String provider_private_key) {
        return new Api2Impl(url, provider_private_key);
    }

    /**
     * Creates a new Version 2 Api object using <code>DEFAULT_3SCALE_PROVIDER_API_URL</code>
     *
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @return A new Api object.
     */
    public static Api2 createV2Api(String provider_private_key) {
        return createV2Api(DEFAULT_3SCALE_PROVIDER_API_URL, provider_private_key);
    }

    /**
     * Creates a new Server Api object, with out any caching.
     *
     * @param url                  URL of the server to connect to. e.g. http://server.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @param sender               The HttpSender object to be used for communication with the server.
     * @return A new Server Api object.
     */
    public static net.threescale.api.v2.Api2 createV2Api(String url, String provider_private_key,
                                                         net.threescale.api.v2.HttpSender sender) {
        return new Api2Impl(url, provider_private_key, sender);
    }

    /**
     * Creates a new Cached Server Api object with a user specified cache and http sender.
     *
     * @param url                  URL of the server to connect to. e.g. http://server.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @param sender               The HttpSender object to be used for communication with the server.
     * @param cache                The cache to use.
     * @return A new Server Api object.
     */
    public static Api2 createV2ApiWithCache(String url, String provider_private_key,
                                            net.threescale.api.v2.HttpSender sender, ApiCache cache) {
        return new Api2Impl(url, provider_private_key, sender, cache);
    }

    /**
     * Creates a new Cached Server Api object using the default local cache parameters in etc/default.xml and
     * a user specified http sender (mainly for testing).
     *
     * @param url                  URL of the server to connect to. e.g. http://server.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @param sender               The HttpSender object to be used for communication with the server.
     * @return A new Server Api object.
     */
    public static Api2 createV2ApiWithLocalCache(String url, String provider_private_key,
                                                 net.threescale.api.v2.HttpSender sender) {
        return new Api2Impl(url, provider_private_key, sender, new DefaultCacheImpl(url, provider_private_key, sender));
    }

    /**
     * Creates a new Cached Server Api object using the default local cache parameters in etc/default.xml
     * and default http sender.
     * @param url                  URL of the server to connect to. e.g. http://server.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @return A new Server Api object.
     */
    public static Api2 createV2ApiWithLocalCache(String url, String provider_private_key) {
        HttpSender sender = new HttpSenderImpl();
        return createV2ApiWithLocalCache(url, provider_private_key, sender);
    }

    /**
     * Creates a Cached Server Api object with cache paramter file specificed by the user.

     * @param url                  URL of the server to connect to. e.g. http://server.3scale.net.
     * @param provider_private_key The Providers private key obtained from 3scale.
     * @param path_to_config       Path to the cache config xml file.
     * @return A new Server Api object.
     */
    public static Api2 createV2ApiWithRemoteCache(String url, String provider_private_key,
                                                  String path_to_config) {
        return new Api2Impl(url, provider_private_key, new ConfiguredCacheImpl(path_to_config, url, provider_private_key, new net.threescale.api.v2.HttpSenderImpl()));
    }

}
