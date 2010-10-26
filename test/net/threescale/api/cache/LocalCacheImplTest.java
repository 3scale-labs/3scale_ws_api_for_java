package net.threescale.api.cache;

import org.junit.Before;

public class LocalCacheImplTest extends CacheImplCommon {

    @Before
    public void setUp() throws Exception {
        cache = new LocalCacheImpl();
    }
}
