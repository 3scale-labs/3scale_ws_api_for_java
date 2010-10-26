package net.threescale.api.cache;

import org.jboss.cache.*;


public class DefaultCacheImpl extends CacheImplCommon implements ApiCache {

    public DefaultCacheImpl() {
        CacheFactory factory = new DefaultCacheFactory();
        cache = factory.createCache("etc/default.xml");
    }


    @Override
     protected void finalize() throws Throwable {
        cache.stop();
        cache.destroy();
        super.finalize();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
