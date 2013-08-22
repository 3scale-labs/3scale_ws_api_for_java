package threescale.v3.api.example;

import threescale.v3.api.*;
import threescale.v3.api.impl.*;


/**
 * Simple Example of using the API
 */
public class AllCallsExample implements TestKeys {

    public static void main(String[] args) {

        runUserKey();
        runAppId();
        runOAuth();
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

        ParameterMap params = new ParameterMap();
        params.add("app_id", oauth_app_id);
        params.add("service_id", oauth_service_id);

        // for OAuth only the '2 steps way' is available
        try {
            AuthorizeResponse response = serviceApi.oauth_authorize(params);  // Perform OAuth authorize
            System.out.println("Authorize on OAuth Success: " + response.success());
            if (response.success() == false) {
                System.out.println("Error: " + response.getErrorCode());
                System.out.println("Reason: " + response.getReason());
            } else {

              // you check the client's secret returned
              System.out.println("OAuth Client Secret: " + response.getClientSecret());

              // the API call got authorized, let's do a report
              ParameterMap transaction = new ParameterMap();
              transaction.add("app_id", oauth_app_id);

              ParameterMap transaction_usage = new ParameterMap();
              transaction_usage.add("hits", "1");
              transaction.add("usage", transaction_usage);

              try {
                  final ReportResponse report_response = serviceApi.report(oauth_service_id, transaction);

                  if (report_response.success()) {
                      System.out.println("Report on OAuth was successful");
                  } else {
                      System.out.println("Report on OAuth failed");
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
}
