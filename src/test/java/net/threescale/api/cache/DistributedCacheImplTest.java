package net.threescale.api.cache;

import net.threescale.api.v2.HttpSenderImpl;
import org.junit.Before;

public class DistributedCacheImplTest extends CacheImplCommonBase {

    @Before
    public void setUp() {
        super.setUp();
        cache = new ConfiguredCacheImpl("etc/test-cache-configuration.xml", SERVER_URL, PROVIDER_KEY, sender);
    }

}
