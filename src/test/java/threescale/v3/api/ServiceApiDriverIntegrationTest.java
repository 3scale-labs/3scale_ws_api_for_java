package threescale.v3.api;

import static org.junit.Assert.assertTrue;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;

import threescale.v3.api.example.TestKeys;
import threescale.v3.api.impl.ServiceApiDriver;

/**
 * Integration Test class for the ServiceApiDriver. Currently only tests 
 * the authrep and report methods and assumes your service uses a user_key
 * Assumes your API has 2 methods getHello and getGoodbye, with getHello 
 * mapped under Hits
 * Set your 3 data items (TestKeys.my_provider_key, TestKeys.user_key_service_id,
 *  TestKeys.user_key) before running
 * Optional: rename the methods below to your own (necessary if your methods are named/configured differently)
 * Run each test individually and examine the Metrics before and after on your 
 * Services overview page in the admin site
   
   
   TODO add tests for the other ServiceApiDriver methods and test the other authentication methods
 */

public class ServiceApiDriverIntegrationTest {
	private final String provider_key = TestKeys.my_provider_key;

	private ServiceApi serviceApi;
//	private ServerAccessor htmlServer;

	DateTimeFormatter fmt;
	ParameterMap params;

	@Before
	public void setup() {
		serviceApi = new ServiceApiDriver(provider_key);

		fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss Z");
		
		params = new ParameterMap();
		params.add("service_id", TestKeys.user_key_service_id);
		params.add("user_key", TestKeys.user_key);

	}

	//@Test 
	// URL Pattern: [usage][hits]=1
	// Expected: OK results in Hits incrementing by 1
	public void testAuthrepNullUsageAndUserKey() throws ServerError {

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);		
	}



	//@Test 
	// URL Pattern: [usage][hits]=1
	// Expected: OK results in Hits incrementing by 1
	public void testAuthrepEmptyUsageAndUserKey() throws ServerError {

		ParameterMap usage = new ParameterMap();
		params.add("usage", usage);

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);		
	}


	//@Test 
	// URL Pattern: [usage][getHello]=2
	// EXPECTED: Success; hits +2
	public void testAuthrepUsageWithNestedMethodAndUserKey() throws ServerError {
		ParameterMap usage = new ParameterMap();
		usage.add("getHello", "2");
		params.add("usage", usage);

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);		
		
	}
	
	//@Test 
	// URL Pattern: [usage][hits]=3&[usage][getHello]=2
	// EXPECTED: Success; hits +5
	public void testAuthrepUsageWithNestedMethodAndHitsAndUserKey() throws ServerError {

		ParameterMap usage = new ParameterMap();
		usage.add("getHello", "2");
		usage.add("hits", "3");
		params.add("usage", usage);

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);
		
	}

	
	//@Test 
	// URL Pattern: [usage][getGoodbye]=4
	// EXPECTED: Success; hits unchanged, getGoodbye +4
	public void testAuthrepUsageWithNonNestedMethodAndUserKey() throws ServerError {

		ParameterMap usage = new ParameterMap();
		usage.add("getGoodbye", "4");
		params.add("usage", usage);

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);
		
	}

	//@Test 
	// URL Pattern: [usage][hits]=1
	// Expected: OK results in Hits incrementing by 1
    public void test_successful_report() throws ServerError {


        ParameterMap usage = new ParameterMap();
        usage.add("hits", "1");
        params.add("usage", usage);

        ReportResponse response = serviceApi.report(TestKeys.user_key_service_id, params);

        assertTrue(response.success());
    }
    

	//@Test 
	// Expected: success
    public void testAuthorizeWithEternityPeriod() throws ServerError {

        AuthorizeResponse response = serviceApi.authorize(params);

        assertTrue(response.success());
        
    }


	//@Test 
	// URL http://requestb.in/1k27m9c1
	// In order to visually examine the request headers in a POST
    // 
	public void testPostToRequestBin() throws ServerError {

		AuthorizeResponse auresp = serviceApi.authrep(params);
		reportResult(auresp);		
	}



	//@Test 
	// URL http://requestb.in/1k27m9c1
	// In order to visually examine the request headers in a GET
    // 
	public void testGetToRequestBin() throws ServerError {
		//String url = "http://requestb.in/1k27m9c1;
		
		ServiceApiDriver localServiceApiDriver = new ServiceApiDriver("provider_key", "http://requestb.in");
		
		localServiceApiDriver.authorize(new ParameterMap());
		
		//reportResult(auresp);		
	}


	
	

    
    //*******************************************************************
	private void reportResult(AuthorizeResponse auresp){
		boolean success = auresp.success(); 
		if (success){
			System.out.println("Success");	
		}
		else{
			System.out.println("Fail: "+auresp.getReason());		
		}		
		
	};

}
