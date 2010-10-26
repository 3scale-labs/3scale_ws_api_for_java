package net.threescale.api.cache;

import net.threescale.api.TestCommon;
import net.threescale.api.v2.AuthorizeResponse;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 17:06:56
 */
public abstract class CacheImplTestCommon extends TestCommon {
    protected ApiCache cache;

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


    @After
    public void tearDown() {
        cache.close();
    }

}
