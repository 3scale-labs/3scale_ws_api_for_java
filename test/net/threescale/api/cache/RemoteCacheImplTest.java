package net.threescale.api.cache;

import org.junit.Before;

public class RemoteCacheImplTest extends CacheImplTestCommon {

    @Before
    public void setUp() throws Exception {
        cache = new RemoteCacheImpl("");
    }
}
