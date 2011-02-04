package net.threescale.api;

import java.util.Date;
import java.util.Map;


/**
 * Used to report a metric to the server @see Api.batch
 */
public class ApiBatchMetric {

    private final String user_key;
    private final Map<String, String> metrics;
    private final Date transactionTime;

    public ApiBatchMetric(String user_key, Map<String, String> metrics) {
        this(user_key, metrics, null);
    }

    public ApiBatchMetric(String user_key, Map<String, String> metrics, Date transactionTime) {
        this.user_key = user_key;
        this.metrics = metrics;
        this.transactionTime = transactionTime;
    }

    /**
     * User Key under which to record the metric.
     * @return  The User key
     */
    public String getUser_key() {
        return user_key;
    }

    /**
     * Key/Value Map for the metrics.
     * Metrics are stored as 'key' 'value'.
     * @return  Map of current metrics.
     */
    public Map<String, String> getMetrics() {
        return metrics;
    }

    /**
     * Optional time when this metric occurred. Null if not set.
     * @return The Date of the transaction
     */
    public Date getTransactionTime() {
        return transactionTime;
    }
}
