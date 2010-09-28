package net.threescale.api.v2;

import net.threescale.api.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 28-Sep-2010
 * Time: 09:20:14
 */
public class HttpSenderTest {

    HttpSenderImpl sender;

    @Mock
    HttpConnectionFactory factory;
    @Mock HttpURLConnection con;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        sender = new HttpSenderImpl(factory);
    }

    @SuppressWarnings({"deprecation"})
    @Test
    public void test_get_returns_200_response() throws IOException {
        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getInputStream()).thenReturn(new java.io.StringBufferInputStream(HAPPY_PATH_RESPONSE));
        when(con.getResponseCode()).thenReturn(200);

        ApiHttpResponse response = sender.sendGetToServer("host1");
        assertEquals(200, response.getResponseCode());
        assertEquals(HAPPY_PATH_RESPONSE, response.getResponseText());

        verify(con).setRequestMethod("GET");
    }

    @SuppressWarnings({"deprecation"})
    @Test
    public void test_get_returns_403_response() throws IOException {
        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getInputStream()).thenReturn(new java.io.StringBufferInputStream(APPLICATION_ID_ERROR_RESPONSE));
        when(con.getResponseCode()).thenReturn(403);

        ApiHttpResponse response = sender.sendGetToServer("host1");
        assertEquals(403, response.getResponseCode());
        assertEquals(APPLICATION_ID_ERROR_RESPONSE, response.getResponseText());

        verify(con).setRequestMethod("GET");
    }

    @Test
    public void test_get_returns_503_response() throws IOException {
        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getResponseMessage()).thenReturn("Service Unavailable");
        when(con.getResponseCode()).thenReturn(503);

        ApiHttpResponse response = sender.sendGetToServer("host1");
        assertEquals(503, response.getResponseCode());
        assertEquals("Service Unavailable", response.getResponseText());

        verify(con).setRequestMethod("GET");
    }

    @Test
    public void test_get_throws_io_error() throws IOException {
        when(factory.openConnection(anyString())).thenThrow(new IOException());

        ApiHttpResponse response = sender.sendGetToServer("host1");
        assertEquals(500, response.getResponseCode());
        assertEquals(ERROR_CONNECTING_RESPONSE, response.getResponseText());
    }

    @Test
    public void test_post_returns_202_response() throws IOException {
        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getInputStream()).thenReturn(new java.io.StringBufferInputStream(""));
        when(con.getResponseCode()).thenReturn(202);

        ApiHttpResponse response = sender.sendPostToServer("host1", "post_data");
        assertEquals(202, response.getResponseCode());
        assertEquals("", response.getResponseText());

        verify(con).setRequestMethod("POST");
        verify(con).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @SuppressWarnings({"deprecation"})
    @Test
    public void test_post_returns_403_response() throws IOException {
        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getInputStream()).thenReturn(new java.io.StringBufferInputStream(REPORT_PROVIDER_ID_INVALID_RESPONSE));
        when(con.getResponseCode()).thenReturn(403);

        ApiHttpResponse response = sender.sendPostToServer("host1", "post_data");
        assertEquals(403, response.getResponseCode());
        assertEquals(REPORT_PROVIDER_ID_INVALID_RESPONSE, response.getResponseText());

        verify(con).setRequestMethod("POST");
        verify(con).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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

    private static final String APPLICATION_ID_ERROR_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "<error code=\"application_not_found\">Application with id=\"12345678\" was not found</error>";

    private static final String REPORT_PROVIDER_ID_INVALID_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "<error code=\"provider_key_invalid\">Provider key \"abcd1234\" is invalid</error>";

    private final String  ERROR_CONNECTING_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
        "<error code=\"server_error\">Could not connect to the server</error>";

}
