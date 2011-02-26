package net.threescale.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This provide two example methods, one for using the Synch Calls,one for the Asynch version.
 */
public class Example {

    // This is YOUR key from your api contract.
    private static String provider_private_key = "3scale-c17d5d3345789afc1fa357ca1e19f26";

    // This is the key from the User in the Html Message, just as constant here for the example code
    private static String user_key = "3scale-c1451ade5789afc1fa357ca1e19f26";

    public static void main(String args[]) {

        Example.sync_example();

        Example.async_example();
    }

    /**
     * This example uses the Sync operations
     */
    public static void sync_example() {
        Api api = ApiFactory.createApi("http://server.3scale.net", provider_private_key);


        try {
            ApiStartResponse transaction = api.start(user_key);

            try {
                // Perform your transaction

                Map<String, String> metrics = new HashMap<String, String>();

                // Set metrics used by the transaction. You define these in your contract.
                metrics.put("requests", "10");
                metrics.put("bytes_transfered", "51983");

                try {
                    api.confirm(transaction.getTransactionId(), metrics);
                } catch (ApiException ex) {
                    // Log application error
                    ex.printStackTrace();
                }
            }
            catch (Exception ex) {
                // Handle exception from application
                try {
                    api.cancel(transaction.getTransactionId());
                } catch (ApiException ex2) {
                    // Log application error
                    ex2.printStackTrace();
                }
            }

        } catch (ApiException e) {
            if (e.getResponseCode() == 403) {
                // Analyse error and return error message to user
            }
        }
    }

    public static void async_example() {
        Api api = ApiFactory.createApi("http://server.3scale.net", provider_private_key);

        try {
            // This call returns the users current usage, you decide whether to allow the transaction or not
            ApiAuthorizeResponse authorize_response = api.authorize(user_key);

            // Do your usage / plan checking here
            if (authorize_response.getPlan().equals("Free")) {
                // Perform transaction and record hits.

                ApiBatchMetric[] batchMetrics = new ApiBatchMetric[1];

                Map<String, String> metric0 = new HashMap<String, String>();

                // Metrics as defined by your plan
                metric0.put("requests", "4");
                metric0.put("bytes_transfered", "221412");
                batchMetrics[0] = new ApiBatchMetric(user_key, metric0, null);

                Map<String, String> metric1 = new HashMap<String, String>();
                metric1.put("hits", "40");
                metric1.put("queries_performed", "48");
                batchMetrics[1] = new ApiBatchMetric("A different User_key", metric0, new Date());

                // Add as many ApiBatchMetric items as you need

                try {
                    // Report the metrics
                    api.batch(batchMetrics);
                } catch (ApiException ex) {
                    ex.printStackTrace();
                    // If any metric is in error, no metrics are recorded, fix the errors and retry
                    // Iterate over ex.getErrors()
                    // and handle / report errors
                }

            } else {
                // Throw some for of exception or inform user of failure.
                // throw new Exception("Not authorised for this plan");
            }
        } catch (ApiException e) {
            //  Handle and report the fact that we cannot connect to the server
            e.printStackTrace();
        }
    }
}

