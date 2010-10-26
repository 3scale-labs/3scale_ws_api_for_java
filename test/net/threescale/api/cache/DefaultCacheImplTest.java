package net.threescale.api.cache;

import org.junit.Before;

public class DefaultCacheImplTest extends CacheImplTestCommon {

    @Before
    public void setUp() throws Exception {
        cache = new DefaultCacheImpl();
    }
}
