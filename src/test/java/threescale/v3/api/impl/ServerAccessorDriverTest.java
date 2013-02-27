package threescale.v3.api.impl;

import org.junit.Test;
import threescale.v3.api.*;

import static org.junit.Assert.assertEquals;

/**
 * This performs real accesses against the server.
 */
public class ServerAccessorDriverTest {
    @Test
    public void testWeCanPerformAGet() {

        ServiceApi serviceApi = new ServiceApiDriver("24e03d2127fd2089220d1bbc45a08ae3");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "30709826");
        try {

            AuthorizeResponse response = serviceApi.authrep(params);
            assertEquals("application_not_found", response.getErrorCode());
            assertEquals("application with id=\"30709826\" was not found", response.getReason());

        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }

    @Test
    public void testWeCanPerformAPost() {

        ServiceApi serviceApi = new ServiceApiDriver("24e03d2127fd2089220d1bbc45a08ae3");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "30709826");
        try {

            ReportResponse response = serviceApi.report(params);
            assertEquals("application_not_found", response.getErrorCode());
            assertEquals("application with id=\"30709826\" was not found", response.getErrorMessage());

        } catch (ServerError serverError) {
        }
    }
}
