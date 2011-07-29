package net.threescale.api.cache;

import net.threescale.api.ApiFactory;
import net.threescale.api.CommonBase;
import net.threescale.api.v2.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CacheTest extends CommonBase {

    @Mock
    private HttpSender sender;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = ApiFactory.createV2ApiWithCache("su1.3scale.net", PROVIDER_KEY, sender, cache);
    }

    private Api2 server;


    @Mock
    private ApiCache cache;

    @Test
    public void authorisatonAccessesServerIfNotInCache() throws Exception {
        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?app_id=" + APP_ID +
                "&provider_key=" + PROVIDER_KEY +
                "&app_key=" + APP_KEY))
                .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));

        when(cache.getAuthorizeFor(APP_ID, APP_KEY, null, null))
                .thenReturn(null);

        AuthorizeResponse authorizeResponse = server.authorize(APP_ID, APP_KEY, null);

        verify(cache).getAuthorizeFor(APP_ID, APP_KEY, null, null);
        verify(cache).addAuthorizedResponse(APP_ID, authorizeResponse, APP_KEY, null, null);
    }

    @Test
    public void reportWithTransactionsAddTransactionsToCache() throws Exception {

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String, String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String, String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransaction("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransaction("bad7e480", "2009-01-01 18:11:59", metrics1);

        server.report(transactions);

        verify(cache).report(transactions);
    }
}
