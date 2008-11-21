package net.threescale.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApiTest {

	private static final String USER_KEY = "52edc747dc99abd99b4d3ffdf51d12ce";
	private static final String GOOD_PROVIDER_KEY = "goodf621b66acb7ec8ceabed4b7aff278";
	private static final String BAD_PROVIDER_KEY = "badfeeeee6acb7eeeeeeeeeed4b7aee160";

	private Api api;
	private Map<String, String> metrics = new HashMap<String, String>();

	private Mockery context = new Mockery();
	private HttpSender sender;

	@Before
	public void setUp() throws Exception {
		sender = context.mock(HttpSender.class);
		api = ApiFactory.createApi("http://beta.3scale.net", GOOD_PROVIDER_KEY,
				sender);
	}

	@After
	public void tearDown() throws Exception {
		context.assertIsSatisfied();
	}

	@Test
	public void test_creation_of_ApiStartResponse_from_xml() {
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
	public void test_missing_values_in_ApiStartResponse_from_xml() {
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
			context.checking(new Expectations() {
				{
					allowing(sender).sendPostToServer(
							with("http://beta.3scale.net/transactions.xml"),
							with("user_key=invalid_key&provider_key="
									+ GOOD_PROVIDER_KEY));
					will(throwException(new ApiException(403,
							"<error id=\"user.invalid_key\">user_key is invalid</error>")));
				}
			});

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

			context.checking(new Expectations() {
				{
					allowing(sender).sendPostToServer(
							with("http://beta.3scale.net/transactions.xml"),
							with("user_key=" + USER_KEY + "&provider_key="
									+ GOOD_PROVIDER_KEY + "&usage[clicksy]=1"));
					will(throwException(new ApiException(404,
							"<error id=\"provider.invalid_metric\">metric does not exist</error>")));
				}
			});

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
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendPostToServer(
									with("http://beta.3scale.net/transactions.xml"),
									with("user_key=" + USER_KEY
											+ "&provider_key="
											+ GOOD_PROVIDER_KEY
											+ "&usage[requests]=1"));
					will(throwException(new ApiException(500,
							"<error id=\"system.other\">Internal Server Error</error>")));
				}
			});

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

		context.checking(new Expectations() {
			{
				allowing(sender).sendPostToServer(
						with("http://beta.3scale.net/transactions.xml"),
						with("user_key=" + USER_KEY + "&provider_key="
								+ GOOD_PROVIDER_KEY + "&usage[requests]=1"));
				will(returnValue(new ApiStartResponse("42", "test",
						"provider_private_key", 200)));
			}
		});

		ApiStartResponse response = api.start(USER_KEY, metrics);
		assertEquals(200, response.getResponseCode());
		assertEquals("test", response.getContractName());
		assertEquals("provider_private_key", response
				.getProviderVerificationKey());
		assertEquals("42", response.getTransactionId());
	}

	@Test
	public void test_confirm_should_raise_exception_on_404_not_found() {

		metrics.put("requests", "1");

		try {
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendPostToServer(
									with("http://beta.3scale.net/transactions/11111111-1111111/confirm.xml"),
									with("provider_key=" + GOOD_PROVIDER_KEY
											+ "&usage[requests]=1"));
					will(throwException(new ApiException(
							404,
							"<error id=\"provider.invalid_transaction_id\">transaction does not exist</error>")));
				}
			});

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
		api = ApiFactory.createApi("http://beta.3scale.net", BAD_PROVIDER_KEY,
				sender);

		metrics.put("requests", "1");
		try {
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendPostToServer(
									with("http://beta.3scale.net/transactions/1/confirm.xml"),
									with("provider_key=" + BAD_PROVIDER_KEY
											+ "&usage[requests]=1"));
					will(throwException(new ApiException(
							403,
							"<error id=\"provider.invalid_key\">provider authentication key is invalid</error>")));
				}
			});

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
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendPostToServer(
									with("http://beta.3scale.net/transactions/1/confirm.xml"),
									with("provider_key=" + GOOD_PROVIDER_KEY
											+ "&usage[requestsyy]=1"));
					will(throwException(new ApiException(400,
							"<error id=\"provider.invalid_metric\">metric does not exist</error>")));
				}
			});

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
		context.checking(new Expectations() {
			{
				allowing(sender)
						.sendPostToServer(
								with("http://beta.3scale.net/transactions/1/confirm.xml"),
								with("provider_key=" + GOOD_PROVIDER_KEY
										+ "&usage[requests]=1"));
				will(returnValue(new ApiStartResponse("", "", "", 200)));
			}
		});

		metrics.put("requests", "1");

		int response = api.confirm("1", metrics);
		assertEquals(200, response);
	}

	@Test
	public void test_cancel_should_raise_exception_on_404_not_found() {
		try {
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendDeleteToServer(
									with("http://beta.3scale.net/transactions/11111111-111111.xml?provider_key="
											+ GOOD_PROVIDER_KEY));
					will(throwException(new ApiException(
							404,
							"<error id=\"provider.invalid_transaction_id\">transaction does not exist</error>")));
				}
			});

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
		api = ApiFactory.createApi("http://beta.3scale.net", BAD_PROVIDER_KEY,
				sender);

		try {
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendDeleteToServer(
									with("http://beta.3scale.net/transactions/1.xml?provider_key="
											+ BAD_PROVIDER_KEY));
					will(throwException(new ApiException(
							403,
							"<error id=\"provider.invalid_key\">provider authentication key is invalid</error>")));
				}
			});

			api.cancel("1");
			fail("Should have thrown an exception");
		} catch (ApiException ex) {
			assertEquals(403, ex.getResponseCode());
			assertEquals("provider.invalid_key", ex.getErrorId());
			assertEquals("provider authentication key is invalid", ex
					.getMessage());
		}
	}

	@Test
	public void test_cancel_should_return_true_on_200_ok() throws ApiException {

		context.checking(new Expectations() {
			{
				allowing(sender)
						.sendDeleteToServer(
								with("http://beta.3scale.net/transactions/1.xml?provider_key="
										+ GOOD_PROVIDER_KEY));
				will(returnValue(200));
			}
		});

		int response = api.cancel("1");
		assertEquals(200, response);
	}

	@Test
	public void test_cancel_should_raise_exception_on_unexpected_response() {
		try {
			context.checking(new Expectations() {
				{
					allowing(sender)
							.sendDeleteToServer(
									with("http://beta.3scale.net/transactions/1.xml?provider_key="
											+ GOOD_PROVIDER_KEY));
					will(throwException(new ApiException(500,
							"<error id=\"system.other\">Internal Server Error</error>")));
				}
			});

			api.cancel("1");

		} catch (ApiException ex) {
			assertEquals(500, ex.getResponseCode());
			assertEquals("system.other", ex.getErrorId());
			assertEquals("Internal Server Error", ex.getMessage());
		}
	}
}
