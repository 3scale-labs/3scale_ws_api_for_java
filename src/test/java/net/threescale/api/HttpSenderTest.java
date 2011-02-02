package net.threescale.api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;

import org.junit.*;
import org.mockito.*;

public class HttpSenderTest {

	HttpSenderImpl sender;
	@Mock
	HttpConnectionFactory factory;
	
	@Mock
	HttpURLConnection con;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		sender = new HttpSenderImpl(factory);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testResponse200ReturnCorrectResponse() throws ApiException, MalformedURLException, IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
			+ "    <transaction>"
			+ "        <id>42</id>"
			+ "        <contract_name>pro</contract_name>"
			+ "        <provider_verification_key>bc43a3e00565d95c297f5ea5028e64e5</provider_verification_key>"
			+ "    </transaction> ";
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(xml));
		when(con.getResponseCode()).thenReturn(200);
		
        ApiHttpResponse response = sender.sendPostToServer("host1", "some data");
		assertEquals(200, response.getResponseCode());
		assertEquals(xml, response.getResponseText());

		verify(con).setRequestMethod("POST");
		verify(con).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testResponse500ThrowsException() throws ApiException, MalformedURLException, IOException {
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(error500XmlResponse));
		when(con.getErrorStream()).thenReturn(new StringBufferInputStream(error500XmlResponse));
		when(con.getResponseCode()).thenReturn(500);
		try {
			sender.sendPostToServer("host1", "some data") ;
			fail("Expected exception");
		}
		catch(ApiException ex) {
			assertEquals(500, ex.getResponseCode());
			assertEquals("System Failure", ex.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testEmptyResponseFromServerThrowsException() throws ApiException, MalformedURLException, IOException {
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(""));
		when(con.getErrorStream()).thenReturn(new StringBufferInputStream(""));
		when(con.getResponseCode()).thenReturn(200);
		try {
			sender.sendPostToServer("host1", "some data") ;
			fail("Expected exception");
		}
		catch(ApiException ex) {
			assertEquals(500, ex.getResponseCode());
			assertEquals("xml error parsing response from server", ex.getMessage());
		}

	}	
    @SuppressWarnings({"deprecation"})
    @Test
    public void testResponseForGet() throws ApiException, IOException {

        when(factory.openConnection(anyString())).thenReturn(con);
        when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(con.getInputStream()).thenReturn(new StringBufferInputStream(authorizeResponseXml));
        when(con.getErrorStream()).thenReturn(new StringBufferInputStream(authorizeResponseXml));
        when(con.getResponseCode()).thenReturn(200);

        String response = sender.sendGetToServer("host1");

        verify(con).setRequestMethod("GET");

        assertEquals(authorizeResponseXml, response);
    }

    String error500XmlResponse =
    "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
    "<error id=\"provider.other\">System Failure</error>";


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
        "      <period_end>2009-08-19 22:59:59</period_end>"+
        "      <current_value>26</current_value>" +
        "      <max_value>100</max_value>" +
        "    </usage>" +
        "  </status>";

}
