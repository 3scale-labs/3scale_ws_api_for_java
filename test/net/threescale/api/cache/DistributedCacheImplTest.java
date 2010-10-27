package net.threescale.api.cache;

import net.threescale.api.v2.AuthorizeResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DistributedCacheImplTest extends CacheImplTestCommon {

    @Before
    public void setUp() throws Exception {
        cache = new ConfiguredCacheImpl("etc/test-cache-configuration.xml");
    }

}
