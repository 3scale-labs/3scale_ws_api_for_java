package net.threescale.api.v2;

import net.threescale.api.ApiFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Example {

    private static String app_url = "http://su1.3scale.net";

    // This is YOUR key from your api contract.
    private static String provider_private_key = "d6989898d7e34a23753b8dcbfa12cbb";
    private static String app_id = "e6581720";
    private static String app_key = "3f51378261d4870669acd2fd80c0b4af";

    private static String invalid_app_id = "e6581744";
    private static String invalid_provider_private_key = "d6989898d7e34a23753b8dcbfa12cdd";

    public static void main(String args[]) {

        new Example().version2_happy_path_example();
        new Example().version2_invalid_app_id();
        new Example().version2_invalid_provider_key_on_authorize();
    }

    /**
     * Executes
     */
    private void version2_happy_path_example() {
        Api2 server = ApiFactory.createV2Api(app_url, app_id, provider_private_key);

        try {
            //
            AuthorizeResponse response = server.authorize(null, null);
            System.out.println("response: " + response.toString());

            // Check that caller has available resources
            if ((currentDailyHits(response) + 1) < maxDailyHits(response)) {

                // Process your api call here

                ApiTransaction[] transactions = new ApiTransaction[1];
                HashMap<String, String> metrics0 = new HashMap<String, String>();
                metrics0.put("hits", "10");

                transactions[0] = new ApiTransaction(app_id, nowTimeStamp(new Date()), metrics0);

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

    private void version2_invalid_app_id() {
        Api2 server = ApiFactory.createV2Api(app_url, invalid_app_id, provider_private_key);

        try {
            AuthorizeResponse response = server.authorize(app_key, null);
            System.out.println("response" + response.toString());
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                    " Message was : " + e.getErrorMessage());
        }
    }

    private void version2_invalid_provider_key_on_authorize() {
        Api2 server = ApiFactory.createV2Api(app_url, app_id, invalid_provider_private_key);

        try {
            AuthorizeResponse response = server.authorize(app_key, null);
            System.out.println("response" + response.toString());
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                    " Message was : " + e.getErrorMessage());
        }
    }

    private String nowTimeStamp(Date timestamp) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormatter.format(timestamp);
    }

    private int maxDailyHits(AuthorizeResponse response) {
        ApiUsageMetric metric = findMetricForHitsPerDay(response);
        return (metric != null) ? Integer.parseInt(metric.getMaxValue()) : 0;
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
}
