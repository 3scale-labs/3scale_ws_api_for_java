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
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
			+ "    <transaction>\n"
			+ "        <id>42</id>\n"
			+ "        <contract_name>pro</contract_name>\n"
			+ "        <provider_verification_key>bc43a3e00565d95c297f5ea5028e64e5</provider_verification_key>\n"
			+ "    </transaction> ";
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(xml));
		when(con.getResponseCode()).thenReturn(200);
		
		ApiStartResponse response = sender.sendPostToServer("host1", "some data");
		assertEquals(200, response.getResponseCode());
		assertEquals("42", response.getTransactionId());
		assertEquals("pro", response.getContractName());
		assertEquals("bc43a3e00565d95c297f5ea5028e64e5", response.getProviderVerificationKey());

		verify(con).setRequestMethod("POST");
		verify(con).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testResponse500ThrowsException() throws ApiException, MalformedURLException, IOException {
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(""));
		when(con.getResponseCode()).thenReturn(500);
		try {
			sender.sendPostToServer("host1", "some data") ;
			fail("Expected exception");
		}
		catch(ApiException ex) {
			assertEquals(500, ex.getResponseCode());
			assertEquals("", ex.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testEmptyResponseFromServerThrowsException() throws ApiException, MalformedURLException, IOException {
		
		when(factory.openConnection(anyString())).thenReturn(con);
		when(con.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(con.getInputStream()).thenReturn(new StringBufferInputStream(""));
		when(con.getResponseCode()).thenReturn(200);
		try {
			sender.sendPostToServer("host1", "some data") ;
			fail("Expected exception");
		}
		catch(ApiException ex) {
			assertEquals(999, ex.getResponseCode());
			assertEquals("xml error parsing response from server", ex.getMessage());
		}

	}	
}
