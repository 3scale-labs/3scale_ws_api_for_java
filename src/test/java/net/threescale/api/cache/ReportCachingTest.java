package net.threescale.api.cache;

import net.threescale.api.CommonBase;
import net.threescale.api.LogFactory;
import net.threescale.api.v2.ApiHttpResponse;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.AuthorizeResponse;
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
import java.util.logging.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


public class ReportCachingTest extends CommonBase {

    Logger log = LogFactory.getLogger(this);

    protected ApiCache api_cache;

    @Mock
    protected HttpSender sender;

 //   @Mock
 //   protected Cache data_cache;

    @Mock
    protected Region region;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        api_cache = new DefaultCacheImpl(SERVER_URL, PROVIDER_KEY, sender);

    }


    @Test
    public void reportTransactions() throws Exception {

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

        ApiTransaction t1 = api_cache.getTransactionFor("bce4c8f4", "2009-01-01 14:23:08");
        assertNotNull("Transaction 1 was not stored in cache", t1);

        ApiTransaction t2 = api_cache.getTransactionFor("bad7e480", "2009-01-01 18:11:59");
        assertNotNull("Transaction 2 was not stored in cache", t2);
    }


}
