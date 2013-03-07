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

        runUserKey();
        runAppId();
        //runOAuth();
    }

    /**
     * Example code for calls on user key (API key) mode
     */
    private static void runUserKey() {
        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap params = new ParameterMap();
        params.add("user_key", user_key);                                 // Add keys for authrep or authorize
        params.add("service_id", user_key_service_id);

        ParameterMap usage = new ParameterMap();                          // Add a metric
        usage.add("hits", "1");

        params.add("usage", usage);

        AuthorizeResponse response = null;
        // the 'preferred way': authrep
        try {
            response = serviceApi.authrep(params);
            System.out.println("AuthRep on User Key Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            }
            System.out.println("Plan: " + response.getPlan());
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }

        // the '2 steps way': authorize + report
        try {
            response = serviceApi.authorize(params);
            System.out.println("Authorize on User Key Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            } else {

              // the API call got authorized, let's do a report
              ParameterMap transaction = new ParameterMap();
              transaction.add("user_key", user_key);

              ParameterMap transaction_usage = new ParameterMap();
              transaction_usage.add("hits", "1");
              transaction.add("usage", transaction_usage);

              try {
                  final ReportResponse report_response = serviceApi.report(user_key_service_id, transaction);

                  if (report_response.success()) {
                      System.out.println("Report on User Key was successful");
                  } else {
                      System.out.println("Report on User Key failed");
                  }
              } catch (ServerError serverError) {
                  serverError.printStackTrace();
              }

            }
            System.out.println("Plan: " + response.getPlan());

        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }
    }

    /**
     * Example code for calls on App Id mode
     */
    private static void runAppId() {
        ServiceApi serviceApi = new ServiceApiDriver(my_provider_key);    // Create the API object

        ParameterMap params = new ParameterMap();
        params.add("app_id", app_id);                                     // Add app_id for authrep or authorize
        params.add("app_key", app_key);                                   // Add key for authrep or authorize
        params.add("service_id", app_id_service_id);

        ParameterMap usage = new ParameterMap();                          // Add a metric
        usage.add("hits", "1");

        params.add("usage", usage);

        AuthorizeResponse response = null;
        // the 'preferred way': authrep
        try {
            response = serviceApi.authrep(params);
            System.out.println("AuthRep on App Id Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            }
            System.out.println("Plan: " + response.getPlan());
        } catch (ServerError serverError) {
            serverError.printStackTrace();
        }

        // the '2 steps way': authorize + report
        try {
            response = serviceApi.authorize(params);
            System.out.println("Authorize on App Id Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            } else {

              // the API call got authorized, let's do a report
              ParameterMap transaction = new ParameterMap();
              transaction.add("app_id", app_id);

              ParameterMap transaction_usage = new ParameterMap();
              transaction_usage.add("hits", "1");
              transaction.add("usage", transaction_usage);

              try {
                  final ReportResponse report_response = serviceApi.report(app_id_service_id, transaction);

                  if (report_response.success()) {
                      System.out.println("Report on App Id was successful");
                  } else {
                      System.out.println("Report on App Id failed");
                  }
              } catch (ServerError serverError) {
                  serverError.printStackTrace();
              }

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
