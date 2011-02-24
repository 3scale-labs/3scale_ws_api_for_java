package net.threescale.api.cache;

import net.threescale.api.CommonBase;
import net.threescale.api.v2.ApiHttpResponse;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.HttpSender;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Region;
import org.jboss.cache.config.EvictionRegionConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class ReportCachingTest extends CommonBase {
    protected ApiCache api_cache;

    @Mock
    protected HttpSender sender;

    @Mock
    protected Cache data_cache;

    @Mock
    protected Region region;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(data_cache.getRegion(any(Fqn.class), anyBoolean())).thenReturn(region);
        api_cache = new DefaultCacheImpl(SERVER_URL, PROVIDER_KEY, sender, data_cache);

    }


    @Test
    public void reportTransactions() throws Exception {
        when(sender.sendPostToServer(anyString(), anyString())).thenReturn(new ApiHttpResponse(202, "202 response"));
        
        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String,  String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String,  String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransaction("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransaction("bad7e480", "2009-01-01 18:11:59", metrics1);

        api_cache.report(transactions);


    }

}
