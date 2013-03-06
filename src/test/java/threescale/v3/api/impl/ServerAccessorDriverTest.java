package threescale.v3.api.impl;

import org.junit.Test;
import threescale.v3.api.*;
import threescale.v3.api.example.TestKeys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This performs real accesses against the server.
 */
public class ServerAccessorDriverTest implements TestKeys {
    @Test
    public void testWeCanPerformAGet() {

        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);

        ParameterMap params = new ParameterMap();
        params.add("app_id", "dummy_app_id");
        try {

            AuthorizeResponse response = serviceApi.authrep(params);
            assertEquals("application_not_found", response.getErrorCode());
            assertEquals("application with id=\"dummy_app_id\" was not found", response.getReason());

        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }

    @Test
    public void testWeCanPerformAPost() {

        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap tr1 = new ParameterMap();
        tr1.add("app_id", app_id);                                        // Set the Users App Id

        ParameterMap usage = new ParameterMap();                          // Create 1st Level PM for usage
        usage.add("hits", "3");                                           // Add number of hits metric
        tr1.add("usage", usage);

        tr1.add("timestamp", "2012-03-01 12:15:31 +01:00");               // Add a time stamp

        try {

            final ReportResponse response = serviceApi.report(null, tr1);

            assertTrue(response.success());
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }
}
