package net.threescale.api.v2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class ApiTest2 {

    private static final String SERVER_URL = "su1.3scale.net";
    private static final String APP_ID = "api-id-ffff";
    private static final String APP_KEY =   "3scale-dsfsdfdsfisodfsdf491e6d941b5b522";
    private static final String USER_KEY = "3scale-bce4c8f4b6578e6c3491e6d941b5b522";
    private static final String PROVIDER_KEY = "goodf621b66acb7ec8ceabed4b7aff278";

    private Api2 server;

    @Mock
    private HttpSender sender;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        server = new Api2Impl("su1.3scale.net", APP_ID, PROVIDER_KEY);
        ((Api2Impl)server).setHttpSender(sender);
    }

    @Test
    public void test_authorize_happy_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                                    "?app_id=" + APP_ID +
                                    "&provider_key=" + PROVIDER_KEY +
                                    "&app_key=" + APP_KEY))
        .thenReturn(new ApiHttpResponse(200, HAPPY_PATH_RESPONSE));


        ApiResponse response = server.authorize(APP_KEY);
        assertEquals(true, response.getAuthorized());
        assertEquals("Basic", response.getPlan());
        assertEquals("", response.getReason());
        assertEquals(2, response.getUsageReports().size());

        assertUsageRecord(response.getUsageReports().get(0), "hits", "month",
                          "2010-08-01 00:00:00 +00:00",
                          "2010-09-01 00:00:00 +00:00",
                          "17344","20000", false);

        assertUsageRecord(response.getUsageReports().get(1), "hits", "day",
                          "2010-08-04 00:00:00 +00:00",
                          "2010-08-05 00:00:00 +00:00",
                          "732","1000", false);

    }

    @Test
    public void test_authorize_exceeded_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                                    "?app_id=" + APP_ID +
                                    "&provider_key=" + PROVIDER_KEY +
                                    "&app_key=" + APP_KEY))
        .thenReturn(new ApiHttpResponse(200, EXCEEDED_PATH_RESPONSE));


        ApiResponse response = server.authorize(APP_KEY);
        assertEquals(false, response.getAuthorized());
        assertEquals("Pro", response.getPlan());
        assertEquals("Usage limits are exceeded", response.getReason());
        assertEquals(2, response.getUsageReports().size());

        assertUsageRecord(response.getUsageReports().get(0), "hits", "month",
                          "2010-08-01 00:00:00 +00:00",
                          "2010-09-01 00:00:00 +00:00",
                          "17344","20000", false);

        assertUsageRecord(response.getUsageReports().get(1), "hits", "day",
                          "2010-08-04 00:00:00 +00:00",
                          "2010-08-05 00:00:00 +00:00",
                          "732","1000", true);

    }

    @Test
    public void test_application_not_found() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                                    "?app_id=" + APP_ID +
                                    "&provider_key=" + PROVIDER_KEY))
        .thenReturn(new ApiHttpResponse(403, APPLICATION_ID_ERROR_RESPONSE));

        ApiResponse response = null;
        try {
            response = server.authorize(null);
            fail("Should have thrown ApiException");
        } catch (ApiException e) {
            assertEquals("application_not_found", e.getErrorCode() );
            assertEquals("Application with id=\"12345678\" was not found", e.getErrorMessage());

        }
    }

    @Test
    public void test_report_happy_path() throws ApiException {

        when(sender.sendPostToServer(SERVER_URL, RESPONSE_HAPPY_PATH_DATA))
        .thenReturn(new ApiHttpResponse(202, null));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String,  String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String,  String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransaction("bce4c8  f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransaction( "bad7e480","2009-01-01 18:11:59", metrics1);

        server.report(transactions);
    }


    @Test
    public void test_report_returns_provider_id_error() {

        when(sender.sendPostToServer(SERVER_URL, RESPONSE_HAPPY_PATH_DATA))
        .thenReturn(new ApiHttpResponse(403, REPORT_PROVIDER_ID_INVALID_RESPONSE));

        ApiTransaction[] transactions = new ApiTransaction[2];
        HashMap<String, String> metrics0 = new HashMap<String,  String>();
        metrics0.put("hits", "1");
        metrics0.put("transfer", "4500");

        HashMap<String, String> metrics1 = new HashMap<String,  String>();
        metrics1.put("hits", "1");
        metrics1.put("transfer", "2840");

        transactions[0] = new ApiTransaction("bce4c8f4", "2009-01-01 14:23:08", metrics0);
        transactions[1] = new ApiTransaction( "bad7e480","2009-01-01 18:11:59", metrics1);

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


    private static final String HAPPY_PATH_RESPONSE =
        "<status>" +
        "  <authorized>true</authorized>" +
        "  <plan>Basic</plan>" +

        "  <usage_reports>" +
        "    <usage_report metric=\"hits\" period=\"month\">" +
        "      <period_start>2010-08-01 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-09-01 00:00:00 +00:00</period_end>" +
        "      <current_value>17344</current_value>" +
        "      <max_value>20000</max_value>" +
        "    </usage_report>" +
        "    <usage_report metric=\"hits\" period=\"day\">" +
        "      <period_start>2010-08-04 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-08-05 00:00:00 +00:00</period_end>" +
        "      <current_value>732</current_value>" +
        "      <max_value>1000</max_value>" +
        "    </usage_report>" +
        "  </usage_reports>" +
        "</status>";

    private static final String EXCEEDED_PATH_RESPONSE = 
        "<status>" +
        "  <authorized>false</authorized>" +
        "  <reason>Usage limits are exceeded</reason>" +
        "  <plan>Pro</plan>" +

        "  <usage_reports>" +
        "    <usage_report metric=\"hits\" period=\"month\">" +
        "      <period_start>2010-08-01 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-09-01 00:00:00 +00:00</period_end>" +
        "      <current_value>17344</current_value>" +
        "      <max_value>20000</max_value>" +
        "    </usage_report>" +
        "    <usage_report metric=\"hits\" period=\"day\" exceeded=\"true\">" +
        "      <period_start>2010-08-04 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-08-05 00:00:00 +00:00</period_end>" +
        "      <current_value>732</current_value>" +
        "      <max_value>1000</max_value>" +
        "    </usage_report>" +
        "  </usage_reports>" +
        "</status>";

    private static final String APPLICATION_ID_ERROR_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "<error code=\"application_not_found\">Application with id=\"12345678\" was not found</error>";

    private static final String RESPONSE_HAPPY_PATH_DATA =
        "transactions[0][app_id]=bce4c8f4&\n" +
        "transactions[0][usage][hits]=1&\n" +
        "transactions[0][usage][transfer]=4500&\n" +
        "transactions[0][timestamp]=2009-01-01%2014%3A23%3A08&\n" +
        "transactions[1][app_id]=bad7e480&\n" +
        "transactions[1][usage][hits]=1&\n" +
        "transactions[1][usage][transfer]=2840&\n" +
        "transactions[1][timestamp]=2009-01-01%2018%3A11%3A59";

    private static final String REPORT_PROVIDER_ID_INVALID_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "<error code=\"provider_key_invalid\">Provider key \"abcd1234\" is invalid</error>";

}

