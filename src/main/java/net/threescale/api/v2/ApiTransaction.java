package net.threescale.api.v2;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Data to be sent for the server for a transaction
 */
public abstract class ApiTransaction {

    public static String APP_ID_TRANSACTION = "app_id";
    public static String USER_KEY_TRANSACTION = "user_key";

    private final String timestamp;
    private final Map<String, String> metrics;


    /**
     * Constructor
     *
     * @param timestamp When the transaction took place
     * @param metrics   What resources were used.
     */
    protected ApiTransaction(String timestamp, Map<String, String> metrics) {
        this.timestamp = timestamp;
        this.metrics = metrics;
    }

    /**
     * Constructor
     *
     * @param metrics What resources were used.
     */
    protected ApiTransaction(Map<String, String> metrics) {
        this.timestamp = Dates.formatDate(new Date());
        this.metrics = metrics;
    }

    public String getApp_id() {
        return getId();
    }
    
    public abstract String getId();
    public abstract String getTransactionType();

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }
}
