package net.threescale.api;

import java.util.Date;
import java.util.Map;


public class ApiBatchMetric {

    private final String user_key;
    private final Map<String, String> metrics;
    private final Date transactionTime;

    public ApiBatchMetric(String user_key, Map<String, String> metrics, Date transactionTime) {
        this.user_key = user_key;
        this.metrics = metrics;
        this.transactionTime = transactionTime;
    }

    public String getUser_key() {
        return user_key;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }
}
