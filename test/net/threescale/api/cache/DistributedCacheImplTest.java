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


    @Test
    public void testCacheExpiresAfter1Second() throws InterruptedException {
        cache.setExpirationInterval(50L);
        
        AuthorizeResponse originalResponse = new AuthorizeResponse(HAPPY_PATH_RESPONSE);
        cache.addAuthorizedResponse(APP_KEY, originalResponse);
        AuthorizeResponse authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertEquals(originalResponse, authorizeResponse);

        Thread.sleep(500L);

        authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertNull(authorizeResponse);
    }


}
