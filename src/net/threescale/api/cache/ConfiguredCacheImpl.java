package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;


public class ConfiguredCacheImpl extends CacheImplCommon implements ApiCache {
    
    public ConfiguredCacheImpl(String path_to_config) {
        CacheFactory factory = new DefaultCacheFactory();
        cache = factory.createCache(path_to_config);
    }
}
