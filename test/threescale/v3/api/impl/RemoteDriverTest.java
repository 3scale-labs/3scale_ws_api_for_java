package threescale.v3.api.impl;

import org.junit.Before;
import org.junit.Test;
import threescale.v3.api.*;

import static org.junit.Assert.assertEquals;

/**
 * User: geoffd
 * Date: 25/02/2013
 */
public class RemoteDriverTest {
    @Test
    public void testWeCanPerformAGet() {

        Client server = new ClientDriver("24e03d2127fd2089220d1bbc45a08ae3");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "30709826");
        try {

            AuthorizeResponse response = server.authrep(params);
            System.out.println("ErrorCode was: " + response.getErrorCode());
            System.out.println("Reason was: " + response.getReason());
            assertEquals("application_not_found", response.getErrorCode());
            assertEquals("application with id=\"30709826\" was not found", response.getReason());

        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }

    @Test
    public void testWeCanPerformAPost() {

        Client server = new ClientDriver("24e03d2127fd2089220d1bbc45a08ae3");

        ParameterMap params = new ParameterMap();
        params.add("app_id", "30709826");
        try {

            ReportResponse response = server.report(params);
            System.out.println("ErrorCode was: " + response.getErrorCode());
            System.out.println("Reason was: " + response.getErrorMessage());
            assertEquals("application_not_found", response.getErrorCode());
            assertEquals("application with id=\"30709826\" was not found", response.getErrorMessage());

        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }
}
