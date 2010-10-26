package net.threescale.api.cache;

import org.jboss.cache.*;


public class DefaultCacheImpl extends CacheImplCommon implements ApiCache {

    public DefaultCacheImpl() {
        CacheFactory factory = new DefaultCacheFactory();
        cache = factory.createCache();
    }

}
