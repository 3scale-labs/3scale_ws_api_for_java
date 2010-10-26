package net.threescale.api.cache;

import net.threescale.api.TestCommon;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocalCacheImplTest extends TestCommon {

    private ApiCache cache;

    @Before
    public void setUp() throws Exception {
        cache = new LocalCacheImpl();
    }

    @Test
    public void testGetAuthorizeForReturnsNullOnFirstAccess() throws Exception {
        AuthorizeResponse authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertNull(authorizeResponse);
    }

    @Test
    public void testGetAuthorizeForReturnsOriginalResponseOnSecondAccess() throws Exception {
        AuthorizeResponse originalResponse = new AuthorizeResponse(HAPPY_PATH_RESPONSE);
        cache.addAuthorizedResponse(APP_KEY, originalResponse);
        AuthorizeResponse authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertEquals(originalResponse, authorizeResponse);
    }
}
