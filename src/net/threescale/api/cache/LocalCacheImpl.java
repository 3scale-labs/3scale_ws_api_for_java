package net.threescale.api.cache;

import org.jboss.cache.*;


public class LocalCacheImpl  extends CacheImplCommon implements ApiCache {

    public LocalCacheImpl() {
        CacheFactory factory = new DefaultCacheFactory();
        cache = factory.createCache();
    }

}
