package net.threescale.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class ApiTest {

    private static final String USER_KEY = "3scale-bce4c8f4b6578e6c3491e6d941b5b522";
    private static final String GOOD_PROVIDER_KEY = "goodf621b66acb7ec8ceabed4b7aff278";
    private static final String BAD_PROVIDER_KEY = "badfeeeee6acb7eeeeeeeeeed4b7aee160";

    private Api api;
    private Map<String, String> metrics = new HashMap<String, String>();

    @Mock
    private HttpSender sender = null;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        api = ApiFactory.createApi("http://server.3scale.net", GOOD_PROVIDER_KEY,
                sender);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_creation_of_ApiStartResponse_from_xml() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                + "    <transaction>\n"
                + "        <id>42</id>\n"
                + "        <contract_name>pro</contract_name>\n"
                + "        <provider_verification_key>bc43a3e00565d95c297f5ea5028e64e5</provider_verification_key>\n"
                + "    </transaction> ";

        ApiStartResponse response = new ApiStartResponse(xml, 200);
        assertEquals("42", response.getTransactionId());
        assertEquals("pro", response.getContractName());
        assertEquals("bc43a3e00565d95c297f5ea5028e64e5", response
                .getProviderVerificationKey());
        assertEquals(200, response.getResponseCode());
    }

    @Test
    public void test_creation_of_ApiException_from_xml() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n"
                + "    <error id=\"user.invalid_key\">user_key is invalid</error> ";

        ApiException exception = new ApiException(200, xml);
        assertEquals("user.invalid_key", exception.getErrorId());
        assertEquals("user_key is invalid", exception.getMessage());
        assertEquals(200, exception.getResponseCode());
    }

    @Test
    public void test_missing_values_in_ApiStartResponse_from_xml() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                + "    <transaction>\n" + "    </transaction> ";

        ApiStartResponse response = new ApiStartResponse(xml, 200);
        assertEquals("", response.getTransactionId());
        assertEquals("", response.getContractName());
        assertEquals("", response.getProviderVerificationKey());
        assertEquals(200, response.getResponseCode());
    }

    @Test
    public void test_start_should_raise_exception_on_403_forbidden() {

        try {
            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions.xml",
                    "user_key=invalid_key&provider_key=" + GOOD_PROVIDER_KEY))
                    .thenThrow(new ApiException(403,
                            "<error id=\"user.invalid_key\">user_key is invalid</error>"));

            api.start("invalid_key");
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals("user.invalid_key", ex.getErrorId());
            assertEquals("user_key is invalid", ex.getMessage());
        }
    }

    @Test
    public void test_start_should_raise_exception_on_400_bad_request() {
        try {
            metrics.put("clicksy", "1");

            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions.xml",
                    "user_key=" + USER_KEY + "&provider_key="
                            + GOOD_PROVIDER_KEY + "&usage[clicksy]=1"))
                    .thenThrow(new ApiException(404,
                            "<error id=\"provider.invalid_metric\">metric does not exist</error>"));

            api.start(USER_KEY, metrics);
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals("provider.invalid_metric", ex.getErrorId());
            assertEquals("metric does not exist", ex.getMessage());
        }
    }

    @Test
    public void test_start_should_raise_exception_on_unexpected_response() {

        metrics.put("requests", "1");
        try {
            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions.xml",
                    "user_key=" + USER_KEY
                            + "&provider_key="
                            + GOOD_PROVIDER_KEY
                            + "&usage[requests]=1"))
                    .thenThrow(new ApiException(500,
                            "<error id=\"system.other\">Internal Server Error</error>"));

            api.start(USER_KEY, metrics);

        } catch (ApiException ex) {
            assertEquals(500, ex.getResponseCode());
            assertEquals("system.other", ex.getErrorId());
            assertEquals("Internal Server Error", ex.getMessage());
        }
    }

    @Test
    public void test_start_should_return_transaction_data_on_200_created()
            throws ApiException {

        metrics.put("requests", "1");

        when(sender.sendPostToServer(
                "http://server.3scale.net/transactions.xml",
                "user_key=" + USER_KEY + "&provider_key="
                        + GOOD_PROVIDER_KEY + "&usage[requests]=1"))
                .thenReturn(new ApiHttpResponse(200, startResponseXml));

        ApiStartResponse response = api.start(USER_KEY, metrics);
        assertEquals(200, response.getResponseCode());
        assertEquals("test", response.getContractName());
        assertEquals("3scale-bc43a3e00565d95c297f5ea5028e64e5", response
                .getProviderVerificationKey());
        assertEquals("42", response.getTransactionId());
    }

    @Test
    public void test_confirm_should_raise_exception_on_404_not_found() {

        metrics.put("requests", "1");

        try {
            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions/11111111-1111111/confirm.xml",
                    "provider_key=" + GOOD_PROVIDER_KEY
                            + "&usage[requests]=1"))
                    .thenThrow(new ApiException(404,
                            "<error id=\"provider.invalid_transaction_id\">transaction does not exist</error>"));

            api.confirm("11111111-1111111", metrics);
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals(404, ex.getResponseCode());
            assertEquals("provider.invalid_transaction_id", ex.getErrorId());
            assertEquals("transaction does not exist", ex.getMessage());
        }
    }

    @Test
    public void test_confirm_should_raise_exception_on_403_forbidden() {
        api = ApiFactory.createApi("http://server.3scale.net", BAD_PROVIDER_KEY,
                sender);

        metrics.put("requests", "1");
        try {
            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions/1/confirm.xml",
                    "provider_key=" + BAD_PROVIDER_KEY
                            + "&usage[requests]=1"))
                    .thenThrow(new ApiException(403,
                            "<error id=\"provider.invalid_key\">provider authentication key is invalid</error>"));

            api.confirm("1", metrics);
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals(403, ex.getResponseCode());
            assertEquals("provider.invalid_key", ex.getErrorId());
            assertEquals("provider authentication key is invalid", ex
                    .getMessage());
        }
    }

    @Test
    public void test_confirm_should_raise_exception_on_400_bad_request() {
        metrics.put("requestsyy", "1");
        try {
            when(sender.sendPostToServer(
                    "http://server.3scale.net/transactions/1/confirm.xml",
                    "provider_key=" + GOOD_PROVIDER_KEY
                            + "&usage[requestsyy]=1"))
                    .thenThrow(new ApiException(400,
                            "<error id=\"provider.invalid_metric\">metric does not exist</error>"));

            api.confirm("1", metrics);
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals(400, ex.getResponseCode());
            assertEquals("provider.invalid_metric", ex.getErrorId());
            assertEquals("metric does not exist", ex.getMessage());
        }
    }

    @Test
    public void test_confirm_should_return_true_on_200_ok() throws ApiException {
        when(sender.sendPostToServer(
                "http://server.3scale.net/transactions/1/confirm.xml",
                "provider_key=" + GOOD_PROVIDER_KEY
                        + "&usage[requests]=1"))
                .thenReturn(new ApiHttpResponse(200, ""));

        metrics.put("requests", "1");

        int response = api.confirm("1", metrics);
        assertEquals(200, response);
    }

    @Test
    public void test_cancel_should_raise_exception_on_404_not_found() {
        try {
            when(sender.sendDeleteToServer(
                    "http://server.3scale.net/transactions/11111111-111111.xml?provider_key="
                            + GOOD_PROVIDER_KEY))
                    .thenThrow(new ApiException(404,
                            "<error id=\"provider.invalid_transaction_id\">transaction does not exist</error>"));

            api.cancel("11111111-111111");
            fail("Should have thrown an exception");
        } catch (ApiException ex) {
            assertEquals(404, ex.getResponseCode());
            assertEquals("provider.invalid_transaction_id", ex.getErrorId());
            assertEquals("transaction does not exist", ex.getMessage());
        }
    }

    @Test
    public void test_cancel_should_raise_exception_on_403_forbidden() {
        api = ApiFactory.createApi("http://server.3scale.net", BAD_PROVIDER_KEY,
                sender);

        try {
            when(sender.sendDeleteToServer(
                    "http://server.3scale.net/transactions/1.xml?provider_key="
                            + BAD_PROVIDER_KEY))
                    .thenThrow(new ApiException(403,
                            "<error id=\"provider.invalid_key\">provider authentication key is invalid</error>"));

            api.cancel("1");
            fail("Should have thrown an exception");
        }
        catch (ApiException ex) {
            assertEquals(403, ex.getResponseCode());
            assertEquals("provider.invalid_key", ex.getErrorId());
            assertEquals("provider authentication key is invalid", ex
                    .getMessage());
        }
    }

    @Test
    public void test_cancel_should_return_true_on_200_ok() throws ApiException {

        when(sender.sendDeleteToServer(
                "http://server.3scale.net/transactions/1.xml?provider_key="
                        + GOOD_PROVIDER_KEY))
                .thenReturn(200);

        int response = api.cancel("1");
        assertEquals(200, response);
    }

    @Test
    public void test_cancel_should_raise_exception_on_unexpected_response() {
        try {
            when(sender.sendDeleteToServer(
                    "http://server.3scale.net/transactions/1.xml?provider_key="
                            + GOOD_PROVIDER_KEY))
                    .thenThrow(new ApiException(500,
                            "<error id=\"system.other\">Internal Server Error</error>"));

            api.cancel("1");

        } catch (ApiException ex) {
            assertEquals(500, ex.getResponseCode());
            assertEquals("system.other", ex.getErrorId());
            assertEquals("Internal Server Error", ex.getMessage());
        }
    }

    @Test
    public void test_authorize_should_return_data_for_valid_request() throws ApiException, ParseException {
        when(sender.sendGetToServer(
                "http://server.3scale.net/transactions/authorize.xml?" +
                        "user_key=" + USER_KEY +
                        "&provider_key=" + GOOD_PROVIDER_KEY))
                .thenReturn(authorizeResponseXml);

        ApiAuthorizeResponse response = api.authorize(USER_KEY);

        assertEquals("Pro", response.getPlan());

        assertEquals(3, response.getUsages().length);

        assertApiUsage("hits", "month", "2009-08-01 00:00:00", "2009-08-31 23:59:59", "17344", "20000", response.getUsages()[0]);
        assertApiUsage("hits", "day", "2009-08-19 00:00:00", "2009-08-19 23:59:59", "732", "1000", response.getUsages()[1]);
        assertApiUsage("hits", "hour", "2009-08-19 22:00:00", "2009-08-19 22:59:59", "26", "100", response.getUsages()[2]);
    }

    @Test
    public void test_authorize_should_raise_exception_on_403_forbidden() {
        api = ApiFactory.createApi("http://server.3scale.net", BAD_PROVIDER_KEY,
                sender);

        try {
            when(sender.sendGetToServer(
                    "http://server.3scale.net/transactions/authorize.xml?user_key=1&provider_key="
                            + BAD_PROVIDER_KEY))
                    .thenThrow(new ApiException(403,
                            "<error id=\"provider.invalid_key\">provider authentication key is invalid</error>"));

            api.authorize("1");
            fail("Should have thrown an exception");
        }
        catch (ApiException ex) {
            assertEquals(403, ex.getResponseCode());
            assertEquals("provider.invalid_key", ex.getErrorId());
            assertEquals("provider authentication key is invalid", ex
                    .getMessage());
        }
    }

    @Test
    public void test_authorize_should_raise_exception_on_500() {
        api = ApiFactory.createApi("http://server.3scale.net", BAD_PROVIDER_KEY,
                sender);

        try {
            when(sender.sendGetToServer(
                    "http://server.3scale.net/transactions/authorize.xml?" +
                            "user_key=" + USER_KEY +
                            "&provider_key=" + GOOD_PROVIDER_KEY))
                    .thenReturn("");

            api.authorize(USER_KEY);
            fail("Should have thrown an exception");
        }
        catch (ApiException ex) {
            assertEquals(500, ex.getResponseCode());
            assertEquals("provider.other", ex.getErrorId());
            assertEquals("Result was empty", ex
                    .getMessage());
        }
    }

    @Test
    public void test_buildBatch_formats_the_data_correctly() throws Exception {
        ApiBatchMetric[] metrics = buildBatchTestMetrics();
        String result = ((ApiImpl) api).buildBatchData(metrics);
        assertEquals(batchPostData, result);
    }

    @Test
    public void test_batch_returns_ok() throws Exception {
        when(sender.sendPostToServer(
                "http://server.3scale.net/transactions.xml", batchPostData))
                .thenReturn(new ApiHttpResponse(201, ""));

        ApiBatchMetric[] metrics = buildBatchTestMetrics();
        int response = api.batch(metrics);
        assertEquals(201, response);
    }

    @Test
    public void test_batch_returns_403() throws Exception {
        when(sender.sendPostToServer(
                "http://server.3scale.net/transactions.xml", batchPostData))
                .thenReturn(new ApiHttpResponse(403, batchErrorReponse));

        ApiBatchMetric[] metrics = buildBatchTestMetrics();
        try {
            api.batch(metrics);
            fail("Should have thrown 403 exception");
        } catch (ApiException ex) {
            assertEquals(403, ex.getResponseCode());
            assertEquals(2, ex.getErrorCount());

            assertEquals("user.invalid_key", ex.getErrors()[0].getId());
            assertEquals(3, ex.getErrors()[0].getIndex());
            assertEquals("user_key is invalid", ex.getErrors()[0].getMessage());

            assertEquals("user.inactive_contract", ex.getErrors()[1].getId());
            assertEquals(15, ex.getErrors()[1].getIndex());
            assertEquals("contract is not active", ex.getErrors()[1].getMessage());
        }
    }

    private ApiBatchMetric[] buildBatchTestMetrics() throws ParseException {
        Map<String, String> m0 = new HashMap<String, String>();
        m0.put("hits", "1");
        m0.put("transfer", "4500");
        Date d0 = stringToDate("2009-01-01 14:23:08");

        Map<String, String> m1 = new HashMap<String, String>();
        m1.put("hits", "1");
        m1.put("transfer", "2840");

        ApiBatchMetric[] metrics = new ApiBatchMetric[2];
        metrics[0] = new ApiBatchMetric(USER_KEY, m0, d0);
        metrics[1] = new ApiBatchMetric(USER_KEY, m1);  // Test optional date
        return metrics;
    }


    private void assertApiUsage(String metric, String period, String periodStart, String periodEnd, String currentValue, String maxValue, ApiUsageMetric apiUsageMetric) throws ParseException {
        assertEquals("metric", metric, apiUsageMetric.getMetric());
        assertEquals("period", period, apiUsageMetric.getPeriod());
        assertEquals("periodStart", stringToDate(periodStart), apiUsageMetric.getPeriodStart());
        assertEquals("periodEnd", stringToDate(periodEnd), apiUsageMetric.getPeriodEnd());
        assertEquals("currentValue", currentValue, apiUsageMetric.getCurrentValue());
        assertEquals("maxValue", maxValue, apiUsageMetric.getMaxValue());
    }

    private Date stringToDate(String value) throws ParseException {
        return ApiUtil.getDataFormatter().parse(value);
    }


    String startResponseXml =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<transaction>" +
                    "  <id>42</id>" +
                    "  <contract_name>test</contract_name>" +
                    "  <provider_verification_key>3scale-bc43a3e00565d95c297f5ea5028e64e5</provider_verification_key>" +
                    "</transaction>";


    String authorizeResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
            "<status>" +
            "  <plan>Pro</plan>" +
            "    <usage metric=\"hits\" period=\"month\">" +
            "      <period_start>2009-08-01 00:00:00</period_start>" +
            "      <period_end>2009-08-31 23:59:59</period_end>" +
            "      <current_value>17344</current_value>" +
            "      <max_value>20000</max_value>" +
            "    </usage>" +
            "    <usage metric=\"hits\" period=\"day\">" +
            "      <period_start>2009-08-19 00:00:00</period_start>" +
            "      <period_end>2009-08-19 23:59:59</period_end>" +
            "      <current_value>732</current_value>" +
            "      <max_value>1000</max_value>" +
            "    </usage>" +
            "    <usage metric=\"hits\" period=\"hour\"> " +
            "      <period_start>2009-08-19 22:00:00</period_start>" +
            "      <period_end>2009-08-19 22:59:59</period_end>" +
            "      <current_value>26</current_value>" +
            "      <max_value>100</max_value>" +
            "    </usage>" +
            "  </status>";

    String batchPostData =
            "provider_key=" + GOOD_PROVIDER_KEY + "&" +
                    "transactions[0][user_key]=3scale-bce4c8f4b6578e6c3491e6d941b5b522&" +
                    "transactions[0][usage][transfer]=4500&" +
                    "transactions[0][usage][hits]=1&" +
                    "transactions[0][timestamp]=2009-01-01%2014:23:08%20+01:00&" +
                    "transactions[1][user_key]=3scale-bce4c8f4b6578e6c3491e6d941b5b522&" +
                    "transactions[1][usage][transfer]=2840&" +
                    "transactions[1][usage][hits]=1";

    String batchErrorReponse =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                    "<errors>" +
                    "<error id=\"user.invalid_key\" index=\"3\">user_key is invalid</error>" +
                    "<error id=\"user.inactive_contract\" index=\"15\">contract is not active</error>" +
                    "</errors>";

}
