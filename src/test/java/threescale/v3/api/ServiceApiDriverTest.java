package threescale.v3.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import threescale.v3.api.impl.ServiceApiDriver;

/**
 * Test class for the Service Api.
 */

public class ServiceApiDriverTest {
    
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final String host = ServiceApi.DEFAULT_HOST;
    private final String provider_key = "1234abcd";

    private ServiceApi serviceApi;
    private ServerAccessor htmlServer;

    DateTimeFormatter fmt;

    @Before
    public void setup() {
        htmlServer = context.mock(ServerAccessor.class);
        serviceApi = new ServiceApiDriver(provider_key).setServer(htmlServer);

        fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss Z");
    }

    @Test
    public void test_default_host() {
        serviceApi = new ServiceApiDriver();

        assertEquals("su1.3scale.net", serviceApi.getHost());
    }

    @Test
    public void test_custom_host() {
        serviceApi = new ServiceApiDriver("1234abcd", "example.com");

        assertEquals("example.com", serviceApi.getHost());
    }

    @Test
    public void test_authrep_usage_is_encoded() throws ServerError {
    	assertAuthrepUrlWithParams("usage%5Bmethod%5D=666");

        ParameterMap params = new ParameterMap();
        ParameterMap usage = new ParameterMap();
        usage.add("method", "666");
        params.add("usage", usage);

        serviceApi.authrep(params);
    }

    @Test
    public void test_authrep_usage_values_are_encoded() throws ServerError {

        assertAuthrepUrlWithParams("usage%5Bhits%5D=%230");

        ParameterMap params = new ParameterMap();
        ParameterMap usage = new ParameterMap();
        usage.add("hits", "#0");
        params.add("usage", usage);

        serviceApi.authrep(params);
    }

    @Test
    public void test_authrep_usage_defaults_to_hits_1() throws ServerError {

        assertAuthrepUrlWithParams("usage%5Bhits%5D=1&app_id=appid");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "appid");

        serviceApi.authrep(params);
    }

    @Test
    public void test_authrep_supports_app_id_app_key_auth_mode() throws ServerError {
        assertAuthrepUrlWithParams("usage%5Bhits%5D=1&app_key=appkey&app_id=appid");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "appid");
        params.add("app_key", "appkey");
        serviceApi.authrep(params);
    }

    @Test
    public void test_successful_authrep_with_app_keys() throws ServerError {
        final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "</status>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authrep.xml?provider_key=1234abcd&usage%5Bhits%5D=1&app_key=toosecret&app_id=foo"));
            will(returnValue(new HttpResponse(200, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        params.add("app_key", "toosecret");

        AuthorizeResponse response = serviceApi.authrep(params);
        assertTrue(response.success());
    }


    @Test
    public void test_successful_authorize() throws ServerError {
        final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "<usage_reports>" +
                "    <usage_report metric=\"hits\" period=\"day\">" +
                "      <period_start>2010-04-26 00:00:00 +0000</period_start>" +
                "      <period_end>2010-04-27 00:00:00 +0000</period_end>" +
                "      <current_value>10023</current_value>" +
                "      <max_value>50000</max_value>" +
                "    </usage_report>" +

                "    <usage_report metric=\"hits\" period=\"month\">" +
                "      <period_start>2010-04-01 00:00:00 +0000</period_start>" +
                "      <period_end>2010-05-01 00:00:00 +0000</period_end>" +
                "      <current_value>999872</current_value>" +
                "      <max_value>150000</max_value>" +
                "    </usage_report>" +
                "  </usage_reports>" +
                "</status>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(200, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        AuthorizeResponse response = serviceApi.authorize(params);

        assertTrue(response.success());
        assertEquals("Ultimate", response.getPlan());
        assertEquals(2, response.getUsageReports().length);

        assertEquals("day", response.getUsageReports()[0].getPeriod());
        assertEquals(fmt.print(new DateTime(2010, 4, 26, 00, 00, DateTimeZone.UTC)), response.getUsageReports()[0].getPeriodStart());
        assertEquals(fmt.print(new DateTime(2010, 4, 27, 00, 00, DateTimeZone.UTC)), response.getUsageReports()[0].getPeriodEnd());
        assertEquals("10023", response.getUsageReports()[0].getCurrentValue());
        assertEquals("50000", response.getUsageReports()[0].getMaxValue());

        assertEquals("month", response.getUsageReports()[1].getPeriod());
        assertEquals(fmt.print(new DateTime(2010, 4, 1, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[1].getPeriodStart());
        assertEquals(fmt.print(new DateTime(2010, 5, 1, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[1].getPeriodEnd());
        assertEquals("999872", response.getUsageReports()[1].getCurrentValue());
        assertEquals("150000", response.getUsageReports()[1].getMaxValue());
    }

    @Test
    public void test_successful_authorize_with_app_keys() throws ServerError {
        final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "</status>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_key=toosecret&app_id=foo"));
            will(returnValue(new HttpResponse(200, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        params.add("app_key", "toosecret");

        AuthorizeResponse response = serviceApi.authorize(params);
        assertTrue(response.success());
    }

    @Test
    public void test_authorize_with_exceeded_usage_limits() throws ServerError {
        final String body = "<status>" +
                "<authorized>false</authorized>" +
                "<reason>usage limits are exceeded</reason>" +

                "<plan>Ultimate</plan>" +

                "<usage_reports>" +
                "  <usage_report metric=\"hits\" period=\"day\" exceeded=\"true\">" +
                "  <period_start>2010-04-26 00:00:00 +0000</period_start>" +
                "  <period_end>2010-04-27 00:00:00 +0000</period_end>" +
                "  <current_value>50002</current_value>" +
                "  <max_value>50000</max_value>" +
                "</usage_report>" +

                "<usage_report metric=\"hits\" period=\"month\">" +
                "  <period_start>2010-04-01 00:00:00 +0000</period_start>" +
                "  <period_end>2010-05-01 00:00:00 +0000</period_end>" +
                "  <current_value>999872</current_value>" +
                "  <max_value>150000</max_value>" +
                "</usage_report>" +
                "</usage_reports>" +
                "</status>";


        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(409, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        AuthorizeResponse response = serviceApi.authorize(params);

        assertFalse(response.success());
        assertTrue("usage limits are exceeded".equals(response.getReason()));
        assertTrue(response.getUsageReports()[0].hasExceeded());
    }

    @Test
    public void test_authorize_with_invalid_app_id() throws ServerError {
        final String body = "<error code=\"application_not_found\">application with id=\"foo\" was not found</error>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(403, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        AuthorizeResponse response = serviceApi.authorize(params);

        assertFalse(response.success());
        assertTrue("application_not_found".equals(response.getErrorCode()));
        assertTrue("application with id=\"foo\" was not found".equals(response.getReason()));
    }


    @Test
    public void test_authorize_metric_period_eternity() throws ServerError {
        final String body = "<status>" +
                "<authorized>true</authorized>" + 
		        "  <plan>Per hit</plan>" + 
		        "  <usage_reports>" + 
		        "    <usage_report metric=\"getGoodbye\" period=\"eternity\">" + 
		        "      <max_value>0</max_value>" + 
		        "      <current_value>0</current_value>" + 
		        "    </usage_report>" + 
		        "  </usage_reports>" + 
		        "</status>";


        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(200, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        AuthorizeResponse response = serviceApi.authorize(params);

        assertTrue(response.success());
        
        assertEquals("Per hit", response.getPlan());
        assertEquals(1, response.getUsageReports().length);

        assertEquals("0", response.getUsageReports()[0].getCurrentValue());
        assertEquals("0", response.getUsageReports()[0].getMaxValue());

        assertEquals("", response.getUsageReports()[0].getPeriodStart());
        assertEquals("", response.getUsageReports()[0].getPeriodEnd());

     }

    @Test(expected = ServerError.class)
    public void test_authorize_with_server_error() throws ServerError {
        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(500, "OMG! WTF!")));
        }});

        // FakeWeb.register_uri(:get, "http://#{@host}/transactions/authorize.xml?provider_key=1234abcd&app_id=foo", :status => ['500', 'Internal Server Error'], :body => 'OMG! WTF!')
        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");

        serviceApi.authorize(params);
    }

    @Test
    public void test_successful_oauth_authorize() throws ServerError {
        final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<application>" +
                "  <id>94bd2de3</id>" +
                "  <key>883bdb8dbc3b6b77dbcf26845560fdbb</key>" +
                "  <redirect_url>http://localhost:8080/oauth/oauth_redirect</redirect_url>" +
                "</application>" +
                "<plan>Ultimate</plan>" +
                "<usage_reports>" +
                "  <usage_report metric=\"hits\" period=\"week\">" +
                "    <period_start>2012-01-30 00:00:00 +0000</period_start>" +
                "    <period_end>2012-02-06 00:00:00 +0000</period_end>" +
                "    <max_value>5000</max_value>" +
                "    <current_value>1</current_value>" +
                "  </usage_report>" +
                "  <usage_report metric=\"update\" period=\"minute\">" +
                "    <period_start>2012-02-03 00:00:00 +0000</period_start>" +
                "    <period_end>2012-02-03 00:00:00 +0000</period_end>" +
                "    <max_value>0</max_value>" +
                "    <current_value>0</current_value>" +
                "  </usage_report>" +
                "</usage_reports>" +
                "</status>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/oauth_authorize.xml?redirect_url=http%3A//localhost%3A8080/oauth/oauth_redirect&provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(200, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        params.add("redirect_url", "http://localhost:8080/oauth/oauth_redirect");

        AuthorizeResponse response = serviceApi.oauth_authorize(params);
        assertTrue(response.success());

        assertEquals("883bdb8dbc3b6b77dbcf26845560fdbb", response.getClientSecret());
        assertEquals("http://localhost:8080/oauth/oauth_redirect", response.getRedirectUrl());

        assertEquals("Ultimate", response.getPlan());
        assertEquals(2, response.getUsageReports().length);

        assertEquals("week", response.getUsageReports()[0].getPeriod());
        assertEquals(fmt.print(new DateTime(2012, 1, 30, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[0].getPeriodStart());
        assertEquals(fmt.print(new DateTime(2012, 02, 06, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[0].getPeriodEnd());
        assertEquals("1", response.getUsageReports()[0].getCurrentValue());
        assertEquals("5000", response.getUsageReports()[0].getMaxValue());

        assertEquals("minute", response.getUsageReports()[1].getPeriod());
        assertEquals(fmt.print(new DateTime(2012, 2, 03, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[1].getPeriodStart());
        assertEquals(fmt.print(new DateTime(2012, 2, 03, 0, 0, DateTimeZone.UTC)), response.getUsageReports()[1].getPeriodEnd());
        assertEquals("0", response.getUsageReports()[1].getCurrentValue());
        assertEquals("0", response.getUsageReports()[1].getMaxValue());
    }

    @Test
    public void test_oauth_authorize_with_exceeded_usage_limits() throws ServerError {
        final String body = "<status>" +
                "<authorized>false</authorized>" +
                "<reason>usage limits are exceeded</reason>" +
                "<application>" +
                "  <id>94bd2de3</id>" +
                "  <key>883bdb8dbc3b6b77dbcf26845560fdbb</key>" +
                "  <redirect_url>http://localhost:8080/oauth/oauth_redirect</redirect_url>" +
                "</application>" +
                "<plan>Ultimate</plan>" +
                "<usage_reports>" +
                "  <usage_report metric=\"hits\" period=\"day\" exceeded=\"true\">" +
                "    <period_start>2010-04-26 00:00:00 +0000</period_start>" +
                "    <period_end>2010-04-27 00:00:00 +0000</period_end>" +
                "    <current_value>50002</current_value>" +
                "    <max_value>50000</max_value>" +
                "  </usage_report>" +

                "  <usage_report metric=\"hits\" period=\"month\">" +
                "    <period_start>2010-04-01 00:00:00 +0000</period_start>" +
                "    <period_end>2010-05-01 00:00:00 +0000</period_end>" +
                "    <current_value>999872</current_value>" +
                "    <max_value>150000</max_value>" +
                "  </usage_report>" +
                "</usage_reports>" +
                "</status>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/oauth_authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(409, body)));
        }});


        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        AuthorizeResponse response = serviceApi.oauth_authorize(params);

        assertFalse(response.success());
        assertEquals("usage limits are exceeded", response.getReason());
        assertTrue(response.getUsageReports()[0].hasExceeded());
    }

    @Test
    public void test_oauth_authorize_with_invalid_app_id() throws ServerError {
        final String body = "<error code=\"application_not_found\">application with id=\"foo\" was not found</error>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/oauth_authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(403, body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");

        AuthorizeResponse response = serviceApi.oauth_authorize(params);

        assertFalse(response.success());
        assertEquals("application_not_found", response.getErrorCode());
        assertEquals("application with id=\"foo\" was not found", response.getReason());
    }

    @Test(expected = ServerError.class)
    public void test_oath_authorize_with_server_error() throws ServerError {

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).get(withUrl("http://" + host + "/transactions/oauth_authorize.xml?provider_key=1234abcd&app_id=foo"));
            will(returnValue(new HttpResponse(500, "OMG! WTF!")));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");

        serviceApi.oauth_authorize(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_report_raises_an_exception_if_no_transactions_given() throws ServerError {
        serviceApi.report(null);
    }

    @Test
    public void test_successful_report() throws ServerError {

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).post(with("http://" + host + "/transactions.xml"), with(any(String.class)));
            will(returnValue(new HttpResponse(202, "")));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        params.add("timestamp", fmt.print(new DateTime(2010, 4, 27, 15, 0)));

        ParameterMap usage = new ParameterMap();
        usage.add("hits", "1");
        params.add("usage", usage);

        ReportResponse response = serviceApi.report(null, params);

        assertTrue(response.success());
    }

    @Test
    public void test_report_encodes_transactions() throws ServerError {

        final String urlParams =
                "transactions%5B0%5D%5Btimestamp%5D=2010-04-27%2015%3A42%3A17%200200" +
                "&transactions%5B0%5D%5Busage%5D%5Bmetric2%5D=2" +
                "&transactions%5B0%5D%5Busage%5D%5Bhits%5D=1" +
                "&transactions%5B0%5D%5Bapp_id%5D=foo" +
                "&transactions%5B1%5D%5Btimestamp%5D=2010-04-27%2015%3A55%3A12%200200" +
                "&transactions%5B1%5D%5Busage%5D%5Bhits%5D=1" +
                "&transactions%5B1%5D%5Bapp_id%5D=bar" +
                "&provider_key=1234abcd";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).post(with("http://" + host + "/transactions.xml"), withParams(urlParams));
            will(returnValue(new HttpResponse(200, "")));
        }});


        ParameterMap app1 = new ParameterMap();
        app1.add("app_id", "foo");
        app1.add("timestamp", "2010-04-27 15:42:17 0200");

        ParameterMap usage1 = new ParameterMap();
        usage1.add("hits", "1");
        usage1.add("metric2", "2");
        app1.add("usage", usage1);

        ParameterMap app2 = new ParameterMap();
        app2.add("app_id", "bar");
        app2.add("timestamp", "2010-04-27 15:55:12 0200");

        ParameterMap usage2 = new ParameterMap();
        usage2.add("hits", "1");
        app2.add("usage", usage2);

        serviceApi.report(null, app1, app2);
    }

    @Test
    public void test_failed_report() throws ServerError {
        final String error_body = "<error code=\"provider_key_invalid\">provider key \"foo\" is invalid</error>";

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).post(with("http://" + host + "/transactions.xml"), with(any(String.class)));
            will(returnValue(new HttpResponse(403, error_body)));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "abc");
        ParameterMap usage = new ParameterMap();
        usage.add("hits", "1");
        params.add("usage", usage);

        ReportResponse response = serviceApi.report(null, params);

        assertFalse(response.success());
        assertEquals("provider_key_invalid", response.getErrorCode());
        assertEquals("provider key \"foo\" is invalid", response.getErrorMessage());
    }

    @Test(expected = ServerError.class)
    public void test_report_with_server_error() throws ServerError {

        context.checking(new UrlWithParamsExpectations() {{
            oneOf(htmlServer).post(with("http://" + host + "/transactions.xml"), with(any(String.class)));
            will(returnValue(new HttpResponse(500, "OMG! WTF!")));
        }});

        ParameterMap params = new ParameterMap();
        params.add("app_id", "foo");
        ParameterMap usage = new ParameterMap();
        usage.add("hits", "1");
        params.add("usage", usage);
        serviceApi.report(null, params);
    }


    private void assertAuthrepUrlWithParams(final String params) throws ServerError {
        final String authrep_url = "http://" + host + "/transactions/authrep.xml?" + params + "&provider_key=" + provider_key;

        final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "</status>";
        //System.out.println("Expect: "+ authrep_url);
        context.checking(new UrlWithParamsExpectations() {
            {
                oneOf(htmlServer).get(withUrl(authrep_url));
                will(returnValue(new HttpResponse(200, body)));
            }
        });
    }
    
    private static class UrlWithParamsExpectations extends Expectations {

        protected String withUrl(String url) {
            currentBuilder().addParameterMatcher(new UrlWithParamsMatcher(url));
            return url;
        }
        
        protected String withParams(String params) {
            currentBuilder().addParameterMatcher(new ParamsMatcher(params));
            return params;
        }
        
    }
    
    private static class UrlWithParamsMatcher extends TypeSafeMatcher<String> {
        
        private final String expectedUrl;

        public UrlWithParamsMatcher(String expectedUrl) {
            this.expectedUrl = expectedUrl;
        }

        @Override
        public boolean matchesSafely(String actualUrl) {
            // Match the first part of the URL (scheme, host, port, etc)
            String expectedUrlHead = expectedUrl.substring(0, expectedUrl.indexOf("?"));
            String actualUrlHead = actualUrl.substring(0, actualUrl.indexOf("?"));
            
            // Match the params (order then compare)
            String expectedParams = expectedUrl.substring(expectedUrl.indexOf("?") + 1);
            String[] expectedParamsSplit = expectedParams.split("&");
            Arrays.sort(expectedParamsSplit);
            String actualParams = actualUrl.substring(actualUrl.indexOf("?") + 1);
            String[] actualParamsSplit = actualParams.split("&");
            Arrays.sort(actualParamsSplit);
            
            List<String> expected = Arrays.asList(expectedParamsSplit);
            List<String> actual = Arrays.asList(actualParamsSplit);
            
            return expected.equals(actual) && expectedUrlHead.equals(actualUrlHead);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a url matching '" + this.expectedUrl + "'");
        }
    }

    private static class ParamsMatcher extends TypeSafeMatcher<String> {
        
        private final String expectedParams;

        public ParamsMatcher(String expectedParams) {
            this.expectedParams = expectedParams;
        }

        @Override
        public boolean matchesSafely(String actualParams) {
            String[] expectedParamsSplit = expectedParams.split("&");
            Arrays.sort(expectedParamsSplit);
            String[] actualParamsSplit = actualParams.split("&");
            Arrays.sort(actualParamsSplit);
            
            List<String> expected = Arrays.asList(expectedParamsSplit);
            List<String> actual = Arrays.asList(actualParamsSplit);
            
            return expected.equals(actual);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("params matching '" + this.expectedParams + "'");
        }
    }

}
