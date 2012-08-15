package net.threescale.api.v2;

import net.threescale.api.ApiFactory;
import net.threescale.api.CommonBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;


public class Api2ImplTest extends CommonBase {


    private Api2 server;

    @Mock
    private HttpSender sender;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = ApiFactory.createV2Api("su1.3scale.net", PROVIDER_KEY, sender);
    }

    @Test
    public void test_authorize_with_app_id_happy_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&app_id=" + APP_ID +
                 "&app_key=" + APP_KEY))
                .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));


        AuthorizeResponse response = server.authorize(APP_ID, APP_KEY, null, null);
        assertEquals(true, response.getAuthorized());
        assertEquals("Basic", response.getPlan());
        assertEquals("", response.getReason());
        assertEquals(2, response.getUsageReports().size());

        assertUsageRecord(response.getUsageReports().get(0), "hits", "month",
                "2010-08-01 00:00:00 +0000",
                "2010-09-01 00:00:00 +0000",
                "17344", "20000", false);

        assertUsageRecord(response.getUsageReports().get(1), "hits", "day",
                "2010-08-04 00:00:00 +0000",
                "2010-08-05 00:00:00 +0000",
                "732", "1000", false);

    }

    @Test
    public void test_authorize_with_user_key_happy_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&user_key=" + USER_KEY))
                .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));


        AuthorizeResponse response = server.authorizeWithUserKey(USER_KEY, null, null);
        assertEquals(true, response.getAuthorized());
        assertEquals("Basic", response.getPlan());
        assertEquals("", response.getReason());
        assertEquals(2, response.getUsageReports().size());

        assertUsageRecord(response.getUsageReports().get(0), "hits", "month",
                "2010-08-01 00:00:00 +0000",
                "2010-09-01 00:00:00 +0000",
                "17344", "20000", false);

        assertUsageRecord(response.getUsageReports().get(1), "hits", "day",
                "2010-08-04 00:00:00 +0000",
                "2010-08-05 00:00:00 +0000",
                "732", "1000", false);

    }

    @Test
    public void test_authorize_exceeded_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&app_id=" + APP_ID +
                "&app_key=" + APP_KEY))
                .thenReturn(new ApiHttpResponse(200, EXCEEDED_PATH_RESPONSE));


        AuthorizeResponse response = server.authorize(APP_ID, APP_KEY, null, null);
        assertEquals(false, response.getAuthorized());
        assertEquals("Pro", response.getPlan());
        assertEquals("Usage limits are exceeded", response.getReason());
        assertEquals(2, response.getUsageReports().size());

        assertUsageRecord(response.getUsageReports().get(0), "hits", "month",
                "2010-08-01 00:00:00 +0000",
                "2010-09-01 00:00:00 +0000",
                "17344", "20000", false);

        assertUsageRecord(response.getUsageReports().get(1), "hits", "day",
                "2010-08-04 00:00:00 +0000",
                "2010-08-05 00:00:00 +0000",
                "732", "1000", true);

    }

    @Test
    public void test_referrer_is_sent_for_authorize() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&app_id=" + APP_ID +
                "&app_key=" + APP_KEY +
                "&referrer=" + REFERRER))
                .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));


        AuthorizeResponse response = server.authorize(APP_ID, APP_KEY, REFERRER, null);
        assertEquals(true, response.getAuthorized());
        assertEquals("Basic", response.getPlan());
        assertEquals("", response.getReason());
    }

    @Test
     public void test_metrics_are_sent_for_authorization() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                 "?provider_key=" + PROVIDER_KEY +
                 "&app_id=" + APP_ID +
                 "&app_key=" + APP_KEY +
                 "&usage[transfer]=1024&usage[hits]=1"))
            .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));

        HashMap<String, String> usage = new HashMap<String, String>();
        usage.put("hits", "1");
        usage.put("transfer", "1024");
        
        AuthorizeResponse response = server.authorize(APP_ID, APP_KEY, null, usage);
        assertNotNull(response);
     }


    @Test
    public void test_application_not_found_on_authorize() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&app_id=" + APP_ID))
                .thenReturn(new ApiHttpResponse(404, APPLICATION_ID_ERROR_RESPONSE));

        try {
            server.authorize(APP_ID, null, null, null);
            fail("Should have thrown ApiException");
        } catch (ApiException e) {
            assertEquals("application_not_found", e.getErrorCode());
            assertEquals("Application with id=\"12345678\" was not found", e.getErrorMessage());

        }
    }

    @Test
    public void test_provider_key_invalid_on_authorize() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                "?provider_key=" + PROVIDER_KEY +
                "&app_id=" + APP_ID))
                .thenReturn(new ApiHttpResponse(403, PROVIDER_KEY_INVALID_ERROR_RESPONSE));

        try {
            server.authorize(APP_ID, null, null, null);
            fail("Should have thrown ApiException");
        } catch (ApiException e) {
            assertEquals("provider_key_invalid", e.getErrorCode());
            assertEquals("Provider key \"abcd1234\" is invalid", e.getErrorMessage());

        }
    }

    @Test
    public void test_report_happy_path_for_app_id() throws ApiException {

        when(sender.sendPostToServer(SERVER_URL, RESPONSE_HAPPY_PATH_DATA_WITH_APP_ID))
                .thenReturn(new ApiHttpResponse(202, null));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String, String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String, String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransactionForAppId("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransactionForAppId("bad7e480", "2009-01-01 18:11:59", metrics1);

        assertEquals(RESPONSE_HAPPY_PATH_DATA_WITH_APP_ID, ApiUtil.formatPostData(PROVIDER_KEY, transactions));

        server.report(transactions);
    }

    @Test
    public void test_report_happy_path_for_user_key() throws ApiException {

        when(sender.sendPostToServer(SERVER_URL, RESPONSE_HAPPY_PATH_DATA_WITH_USER_KEY))
                .thenReturn(new ApiHttpResponse(202, null));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String, String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String, String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransactionForUserKey("asdfsdf", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransactionForUserKey("dsssddd", "2009-01-01 18:11:59", metrics1);

        assertEquals(RESPONSE_HAPPY_PATH_DATA_WITH_USER_KEY, ApiUtil.formatPostData(PROVIDER_KEY, transactions));

        server.report(transactions);
    }


    @Test
    public void test_report_returns_provider_id_error() {

        when(sender.sendPostToServer(SERVER_URL, RESPONSE_HAPPY_PATH_DATA_WITH_APP_ID))
                .thenReturn(new ApiHttpResponse(403, PROVIDER_KEY_INVALID_ERROR_RESPONSE));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String, String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String, String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransactionForAppId("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransactionForAppId("bad7e480", "2009-01-01 18:11:59", metrics1);

        try {
            server.report(transactions);
            fail("Should have thrown ApiException");
        } catch (ApiException e) {
            assertEquals(e.getErrorCode(), "provider_key_invalid");
            assertEquals(e.getErrorMessage(), "Provider key \"abcd1234\" is invalid");
        }
    }

    private void assertUsageRecord(net.threescale.api.v2.ApiUsageMetric usage, String metric, String period, String period_start, String period_end, String current_value, String max_value, Boolean exceeded) {
        assertEquals(metric, usage.getMetric());
        assertEquals(period, usage.getPeriod());
        assertEquals(period_start, usage.getPeriodStart());
        assertEquals(period_end, usage.getPeriodEnd());
        assertEquals(current_value, usage.getCurrentValue());
        assertEquals(max_value, usage.getMaxValue());
        assertEquals(exceeded, usage.getExceeded());
    }


    private static final String EXCEEDED_PATH_RESPONSE =
            "<status>" +
                    "  <authorized>false</authorized>" +
                    "  <reason>Usage limits are exceeded</reason>" +
                    "  <plan>Pro</plan>" +

                    "  <usage_reports>" +
                    "    <usage_report metric=\"hits\" period=\"month\">" +
                    "      <period_start>2010-08-01 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-09-01 00:00:00 +0000</period_end>" +
                    "      <current_value>17344</current_value>" +
                    "      <max_value>20000</max_value>" +
                    "    </usage_report>" +
                    "    <usage_report metric=\"hits\" period=\"day\" exceeded=\"true\">" +
                    "      <period_start>2010-08-04 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-08-05 00:00:00 +0000</period_end>" +
                    "      <current_value>732</current_value>" +
                    "      <max_value>1000</max_value>" +
                    "    </usage_report>" +
                    "  </usage_reports>" +
                    "</status>";

    private static final String APPLICATION_ID_ERROR_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<error code=\"application_not_found\">Application with id=\"12345678\" was not found</error>";

    private static final String PROVIDER_KEY_INVALID_ERROR_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<error code=\"provider_key_invalid\">Provider key \"abcd1234\" is invalid</error>";

}

