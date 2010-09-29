package net.threescale.api.v2;

import java.util.HashMap;

/**
 * Data to be sent for the server for a transaction
 *
 */
public class ApiTransaction {
    private final String app_id;
    private final String timestamp;
    private final HashMap<String, String> metrics;


    /**
     * Constructor
     * @param app_id Application ID for this report
     * @param timestamp When the transaction took place
     * @param metrics What resources were used.
     */
    public ApiTransaction(String app_id, String timestamp, HashMap<String, String> metrics) {
        this.app_id = app_id;
        this.timestamp = timestamp;
        this.metrics = metrics;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public HashMap<String, String> getMetrics() {
        return metrics;
    }
}
