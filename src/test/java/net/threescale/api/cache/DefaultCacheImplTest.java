package net.threescale.api.cache;

import org.junit.Before;

public class DefaultCacheImplTest extends CacheImplCommonBase {

    @Before
    public void setUp() {
        super.setUp();
        cache = new DefaultCacheImpl(SERVER_URL, PROVIDER_KEY, sender);
    }
}
