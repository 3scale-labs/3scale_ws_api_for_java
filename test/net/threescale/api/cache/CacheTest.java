package net.threescale.api.cache;

import net.threescale.api.ApiFactory;
import net.threescale.api.CommonBase;
import net.threescale.api.v2.Api2;
import net.threescale.api.v2.ApiHttpResponse;
import net.threescale.api.v2.AuthorizeResponse;
import net.threescale.api.v2.HttpSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;


public class CacheTest extends CommonBase {

    @Mock
    private HttpSender sender;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = ApiFactory.createV2ApiWithCache("su1.3scale.net", APP_ID, PROVIDER_KEY, sender, cache);
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

        when(cache.getAuthorizeFor(APP_KEY))
                .thenReturn(null);

        AuthorizeResponse authorizeResponse = server.authorize(APP_KEY, null);

        verify(cache).getAuthorizeFor(APP_KEY);
        verify(cache).addAuthorizedResponse(APP_KEY, authorizeResponse);
    }

}
