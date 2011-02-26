package net.threescale.api.cache;

import net.threescale.api.v2.HttpSender;
import org.jboss.cache.DefaultCacheFactory;


public class ConfiguredCacheImpl extends CacheImplCommon implements ApiCache {

    public ConfiguredCacheImpl(String path_to_config, String host_url, String provider_key, HttpSender sender) {
        super(host_url, provider_key, sender, new DefaultCacheFactory().createCache(path_to_config));
    }
}
