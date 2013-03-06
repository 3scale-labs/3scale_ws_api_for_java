package threescale.v3.api.example;

import threescale.v3.api.*;
import threescale.v3.api.impl.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Simple Example of using the API
 */
public class AllCallsExample implements TestKeys {


    public static void main(String[] args) {

        runAuthRep();
        runOAuth();
        runReport();
    }

    /**
     * Example code for an AuthRep
     */
    private static void runAuthRep() {
        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap params = new ParameterMap();                         // Add keys for the autorize
        params.add("app_key", app_key);
        params.add("user_key", user_key);
        params.add("service_id", service_id);

        ParameterMap usage = new ParameterMap();                          // Add a hit metric
        usage.add("hits", "1");

        params.add("usage", usage);

        AuthorizeResponse response = null;
        try {
            response = serviceApi.authrep(params);                        // Perform authRep
            System.out.println("Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            }
            System.out.println("Plan: " + response.getPlan());
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }

    /**
     * Example code for an OAuth authorize
     */
    private static void runOAuth() {
        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap params = new ParameterMap();                         // Add keys
        params.add("app_id", oa_app_id);
        params.add("client_id", client_id);
        params.add("secret", secret);
        params.add("service_id", oa_service_id);

        try {
            AuthorizeResponse response = serviceApi.oauth_authorize(params);  // Perform OAuth authorize
            System.out.println("Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            }
            System.out.println("Plan: " + response.getPlan());
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }


    /**
     * Example code for a Report call
     */
    private static void runReport() {
        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap tr1 = new ParameterMap();
        tr1.add("app_id", app_id);                                        // Set the Users App Id

        ParameterMap usage = new ParameterMap();                          // Create 1st Level PM for usage
        usage.add("hits", "3");                                           // Add number of hits metric
        tr1.add("usage", usage);

        tr1.add("timestamp", "2012-03-01 12:15:31 +01:00");               // Add a time stamp

        try {

            final ReportResponse response = serviceApi.report(service_id, tr1);

            if (response.success()) {                                       // Check if the Report succeeded
                System.out.println("Report was successful");
            } else {
                System.out.println("Report failed");
            }
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }


}
