package net.threescale.api.v2;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Data to be sent for the server for a transaction
 */
public class ApiTransaction {
    private final String app_id;
    private final String timestamp;
    private final Map<String, String> metrics;


    /**
     * Constructor
     *
     * @param app_id    Application ID for this report
     * @param timestamp When the transaction took place
     * @param metrics   What resources were used.
     */
    public ApiTransaction(String app_id, String timestamp, Map<String, String> metrics) {
        this.app_id = app_id;
        this.timestamp = timestamp;
        this.metrics = metrics;
    }

    /**
     * Constructor
     *
     * @param app_id  Application ID for this report
     * @param metrics What resources were used.
     */
    public ApiTransaction(String app_id, Map<String, String> metrics) {
        this.app_id = app_id;
        this.timestamp = Dates.formatDate(new Date());
        this.metrics = metrics;
    }

    public static ApiTransaction buildSingletonMetricApiTransaction(String app_id, String metricName, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap(metricName, metricValue);
        return new ApiTransaction(app_id, metrics);
    }

    public static ApiTransaction buildHitsMetricApiTransaction(String app_id, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap("hits", metricValue);
        return new ApiTransaction(app_id, metrics);
    }

    public String getApp_id() {
        return app_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }
}
