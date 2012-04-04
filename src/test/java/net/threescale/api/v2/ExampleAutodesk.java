package net.threescale.api.v2;

import net.threescale.api.ApiFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ExampleAutodesk {

    private static String app_url = "http://su1.3scale.net";

    // This is YOUR key from your api contract.
    private static String provider_private_key = "autodesk-1d2d2b4d0211e685607693ea61a98d8a";
    private static String app_id = "94bd2de3";
    private static String app_key = "190e58d31f4f8fe173d8cbfc7b59cf93";

    private static String invalid_app_id = "e6581744";
    private static String invalid_provider_private_key = "d6989898d7e34a23753b8dcbfa12cdd";

    public static void main(String args[]) {

        //new Example().happy_path_example_with_no_cache();
        new ExampleAutodesk().happy_path_example_using_local_cache();
        //new Example().happy_path_example_using_remote_or_custom_cache();

        //new Example().example_with_invalid_app_id();
        //new Example().example_with_invalid_provider_key_on_authorize();
    }

    private void happy_path_example_with_no_cache() {
        Api2 server = ApiFactory.createV2Api(app_url, provider_private_key);
        for (int i = 0; i < 2; i++) {
            executeHappyPath(server);
        }
    }

    private void happy_path_example_using_local_cache() {
        Api2 server = ApiFactory.createV2ApiWithLocalCache(app_url, provider_private_key);
        for (int i = 0; i < 10; i++) {
            sleep(10L, 600L);
            System.out.println("NOTE ******** iteration: " + i + "****************************************************");
            executeHappyPath(server);
        }
        sleep(1000L, 2000L);
        executeHappyPath(server);
        sleep(1000L, 2000L);
     }


    private void happy_path_example_using_remote_or_custom_cache() {

        /* Remote caches are usually quite specific to the application. Please refer to the JBoss Cache
           documentation on how to setup a remote cache and the options available.
        */
        Api2 server = ApiFactory.createV2ApiWithRemoteCache(app_url, provider_private_key, "etc/config-samples/buddy-replication.xml");
        executeHappyPath(server);
    }


    /**
     * Executes
     */
    private void executeHappyPath(Api2 server) {
        try {
            //
            AuthorizeResponse response = server.authorize(app_id, app_key, null, null);
            System.out.println("response: hits = " + response.currentHits());

            // Check that caller has available resources
            //if ((currentDailyHits(response) + 1) < maxDailyHits(response)) {
            if (true) {

                // Process your api call here

                ApiTransaction[] transactions = new ApiTransaction[1];
                HashMap<String, String> metrics0 = new HashMap<String, String>();
                metrics0.put("hits", "1");

                transactions[0] = new ApiTransactionForAppId(app_id, nowTimeStamp(new Date()), metrics0);

                server.report(transactions);
            } else {
                // Report error to caller.
                throw new RuntimeException("Insufficient 'hits' available");
            }

        } catch (ApiException e) {
            if (e.getErrorCode() == "404") {
                // app_id is invalid
                throw new RuntimeException(e.getErrorMessage());
            } else if (e.getErrorCode() == "403") {
                // provider_key is invalid
                throw new RuntimeException(e.getErrorMessage());
            } else {
                // Some other error, handle as needed.
                e.printStackTrace();
            }
        }
    }

    private void example_with_invalid_app_id() {
        Api2 server = ApiFactory.createV2Api(app_url,  provider_private_key);

        try {
            AuthorizeResponse response = server.authorize(invalid_app_id, app_key, null, null);
            System.out.println("response" + response.toString());
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                    " Message was : " + e.getErrorMessage());
        }
    }

    private void example_with_invalid_provider_key_on_authorize() {
        Api2 server = ApiFactory.createV2Api(app_url, invalid_provider_private_key);

        try {
            AuthorizeResponse response = server.authorize(app_id, app_key, null, null);
            System.out.println("response" + response.toString());
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                    " Message was : " + e.getErrorMessage());
        }
    }




    private String nowTimeStamp(Date timestamp) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS ");
        return dateFormatter.format(timestamp);
    }

    private int maxDailyHits(AuthorizeResponse response) {
        ApiUsageMetric metric = findMetricForHitsPerDay(response);
        return (metric != null) ? Integer.parseInt(metric.getMaxValue()) : 1000000;
    }

    private int currentDailyHits(AuthorizeResponse response) {
        ApiUsageMetric metric = findMetricForHitsPerDay(response);
        return (metric != null) ? Integer.parseInt(metric.getCurrentValue()) : 0;
    }

    private ApiUsageMetric findMetricForHitsPerDay(AuthorizeResponse response) {
        return findMetricForPeriod(response.getUsageReports(), "hits", "minute");
    }

    // Find a specific metric/period usage metric

    private ApiUsageMetric findMetricForPeriod(ArrayList<ApiUsageMetric> usage_reports, String metric_key, String period_key) {
        for (ApiUsageMetric metric : usage_reports) {
            if (metric.getMetric().equals(metric_key) && metric.getPeriod().equals(period_key)) {
                return metric;
            }
        }
        return null;
    }
    
    private void sleep(long minMillseconds, long maxMilliseconds){
        long possibleSleepTime = (long)(Math.random()* maxMilliseconds);  
        long sleepTime = (possibleSleepTime > minMillseconds)? possibleSleepTime : minMillseconds;

        try {
                Thread.currentThread().sleep(sleepTime);  
                } catch (Exception e) {
                    // do nothing
                }
    }
}
