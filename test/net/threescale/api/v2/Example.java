package net.threescale.api.v2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 29-Sep-2010
 * Time: 10:41:10
 */
public class Example {

    private static String app_url = "http://su1.3scale.net";

    // This is YOUR key from your api contract.
    private static String provider_private_key = "6d70ddea3d7e34a23753b8dcbfa12cbb";
    private static String app_id = "e6581720";
    private static String app_key = "3f51378261d4870669acd2fd80c0b4af";

    private static String invalid_app_id = "e6581744";
    private static String invalid_provider_private_key = "6d70ddea3d7e34a23753b8dcbfa12cdd";

    public static void main(String args[]) {

        new Example().version2_happy_path_example();
        new Example().version2_invalid_app_id();
        new Example().version2_invalid_provider_key();

    }

    private void version2_happy_path_example() {
        Api2 server = new Api2Impl(app_url, app_id, provider_private_key);

        try {
            ApiResponse response = server.authorize(app_key, null);
            System.out.println("response" + response.toString());

            if ((currentDailyHits(response) + 10) < maxDailyHits(response)) {

                // Process your api call here

                ApiTransaction[] transactions = new ApiTransaction[1];
                HashMap<String, String> metrics0 = new HashMap<String,  String>();
                metrics0.put("hits", "10");

                transactions[0] = new ApiTransaction(app_id, nowTimeStamp(new Date()), metrics0);

                server.report(transactions);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void version2_invalid_app_id() {
        Api2 server = new Api2Impl(app_url, invalid_app_id, provider_private_key);

        try {
            ApiResponse response = server.authorize(app_key, null);
            System.out.println("response" + response.toString());

            if ((currentDailyHits(response) + 10) < maxDailyHits(response)) {

                // Process your api call here

                ApiTransaction[] transactions = new ApiTransaction[1];
                HashMap<String, String> metrics0 = new HashMap<String,  String>();
                metrics0.put("hits", "10");

                transactions[0] = new ApiTransaction(app_id, nowTimeStamp(new Date()), metrics0);

                server.report(transactions);
            }
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                               " Message was : " + e.getErrorMessage()); 
        }
    }

    private void version2_invalid_provider_key() {
        Api2 server = new Api2Impl(app_url, app_id, invalid_provider_private_key);

        try {
            ApiResponse response = server.authorize(app_key, null);
            System.out.println("response" + response.toString());

            if ((currentDailyHits(response) + 10) < maxDailyHits(response)) {

                // Process your api call here

                ApiTransaction[] transactions = new ApiTransaction[1];
                HashMap<String, String> metrics0 = new HashMap<String,  String>();
                metrics0.put("hits", "10");

                transactions[0] = new ApiTransaction(app_id, nowTimeStamp(new Date()), metrics0);

                server.report(transactions);
            }
        } catch (ApiException e) {
            System.out.println("ApiException: responseCode was: " + e.getErrorCode() +
                               " Message was : " + e.getErrorMessage());
        }
    }

    private String nowTimeStamp(Date timestamp) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = dateFormatter.format(timestamp);
        System.out.println("Formatted Date: " + dt);
        return dt;
    }

    private int maxDailyHits(ApiResponse response) {
        for( ApiUsageMetric metric : response.getUsageReports()) {
            if (metric.getMetric().equals("hits") && metric.getPeriod().equals("day")) {
                return Integer.parseInt(metric.getMaxValue());
            }
        }
        return 0;
    }

    private int currentDailyHits(ApiResponse response) {
        for( ApiUsageMetric metric : response.getUsageReports()) {
            if (metric.getMetric().equals("hits") && metric.getPeriod().equals("day")) {
                return Integer.parseInt(metric.getCurrentValue());
            }
        }
        return 0;
    }


}
