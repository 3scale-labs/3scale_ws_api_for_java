package net.threescale.api.v2;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 17:22:03
 */
public class ApiTransaction {
    private final String app_id;
    private final String timestamp;
    private final HashMap<String, String> metrics;

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
