package net.threescale.api;

import net.threescale.api.v2.Api2;
import net.threescale.api.v2.Api2Impl;
import net.threescale.api.v2.ApiException;
import net.threescale.api.v2.ApiResponse;
import net.threescale.api.v2.ApiUsageReport;
import net.threescale.api.v2.HttpSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        server = new Api2Impl("su1.3scale.net");
    }

    @Test
    public void test_authorize_happy_path() throws ApiException {

        when(sender.sendGetToServer(SERVER_URL + "/transactions/authorize.xml" +
                                    "?app_id=" + APP_ID +
                                    "&provider_key=" + PROVIDER_KEY +
                                    "&app_key=" + APP_KEY))
        .thenReturn(HAPPY_PATH_RESPONSE);


        ApiResponse response = server.authorize(APP_ID, PROVIDER_KEY, APP_KEY);
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
        .thenReturn(EXCEEDED_PATH_RESPONSE);


        ApiResponse response = server.authorize(APP_ID, PROVIDER_KEY, APP_KEY);
        assertEquals(false, response.getAuthorized());
        assertEquals("Pro", response.getPlan());
        assertEquals("", response.getReason());
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
        .thenReturn(APPLICATION_ID_ERROR_RESPONSE);


        ApiResponse response = null;
        try {
            response = server.authorize(APP_ID, PROVIDER_KEY, APP_KEY);
            fail("Should have thrown ApiException");
        } catch (ApiException e) {
            assertEquals(e.getErrorCode(), "application_not_found");
            assertEquals(e.getErrorMessage(), "Application with id=\"12345678\" was not found");

        }
    }

    


    private void assertUsageRecord(ApiUsageReport usage, String metric, String period, String period_start, String period_end, String current_value, String max_value, Boolean exceeded) {
        assertEquals(usage.getMetric(), metric);
        assertEquals(usage.getPeriod(), period);
        assertEquals(usage.getPeriodStart(), period_start);
        assertEquals(usage.getPeriodEnd(), period_end);
        assertEquals(usage.getCurrentValue(), current_value);
        assertEquals(usage.getMaxValue(), max_value);
        assertEquals(usage.getExceeded(), exceeded);
    }


    private static final String HAPPY_PATH_RESPONSE =
        "<status>" +
        "  <authorized>true</authorized>" +
        "  <plan>Basic</plan>" +

        "  <usage_reports>" +
        "    <usage_report metric=\"hits\" period=\"month\">" +
        "      <period_start>2010-08-01 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-09-01 00:00:00 +00:00</period_end>" +
        "      <current_value>17344<current_value>" +
        "      <max_value>20000</max_value>" +
        "    </usage_report>" +
        "    <usage_report metric=\"hits\" period=\"day\">" +
        "      <period_start>2010-08-04 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-08-05 00:00:00 +00:00</period_end>" +
        "      <current_value>732</current_value>" +
        "      <max_value>1000</max_value>" +
        "    </usage_report>" +
        "  </usage_reports/>" +
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
        "      <current_value>17344<current_value>" +
        "      <max_value>20000</max_value>" +
        "    </usage_report>" +
        "    <usage_report metric=\"hits\" period=\"day\">" +
        "      <period_start>2010-08-04 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-08-05 00:00:00 +00:00</period_end>" +
        "      <current_value>732</current_value>" +
        "      <max_value>1000</max_value>" +
        "    </usage_report>" +
        "  </usage_reports/>" +
        "</status>";

    private static final String APPLICATION_ID_ERROR_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "<error code=\"application_not_found\">Application with id=\"12345678\" was not found</error>";

}

