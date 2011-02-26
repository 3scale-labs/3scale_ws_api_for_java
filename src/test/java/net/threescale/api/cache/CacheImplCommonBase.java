package net.threescale.api.cache;

import net.threescale.api.CommonBase;
import net.threescale.api.v2.ApiHttpResponse;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 17:06:56
 */
public abstract class CacheImplCommonBase extends CommonBase {
    protected ApiCache cache;

    @Mock
    protected HttpSender sender;

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

    @Test
    public void testCacheExpiresAfter50thOfASecond() throws InterruptedException {
        cache.setAuthorizeExpirationInterval(50L);

        AuthorizeResponse originalResponse = new AuthorizeResponse(HAPPY_PATH_RESPONSE);
        cache.addAuthorizedResponse(APP_KEY, originalResponse);
        AuthorizeResponse authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertEquals(originalResponse, authorizeResponse);

        Thread.sleep(500L);

        authorizeResponse = cache.getAuthorizeFor(APP_KEY);
        assertNull(authorizeResponse);
    }

    @Test
    public void reportWithTransactionsAddTransactionsToCache() throws Exception {

        when(sender.sendPostToServer(anyString(), anyString())).thenReturn(new ApiHttpResponse(202, "202 response"));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String, String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String, String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransaction("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransaction("bad7e480", "2009-01-01 18:11:59", metrics1);

        cache.report(transactions);

    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        cache.close();
    }

}
