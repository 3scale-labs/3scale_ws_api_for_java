package net.threescale.api.cache;

import net.threescale.api.v2.HttpSender;
import org.jboss.cache.*;


public class DefaultCacheImpl extends CacheImplCommon implements ApiCache {

    public DefaultCacheImpl(String host_url, String provider_key, HttpSender sender) {
        super(host_url, provider_key, sender);
        data_cache = new DefaultCacheFactory().createCache("etc/default.xml");
    }


    @Override
     protected void finalize() throws Throwable {
        data_cache.stop();
        data_cache.destroy();
        super.finalize();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
