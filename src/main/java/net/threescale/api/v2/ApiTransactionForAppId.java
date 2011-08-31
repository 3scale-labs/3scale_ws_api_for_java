package net.threescale.api.v2;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ApiTransactionForAppId extends ApiTransaction {

    private String app_id;
    /**
     * Constructor
     *
     * @param app_id    Application ID for this report
     * @param timestamp When the transaction took place
     * @param metrics   What resources were used.
     */
    public ApiTransactionForAppId(String app_id, String timestamp, Map<String, String> metrics) {
        super(timestamp, metrics);
        this.app_id = app_id;
   }

    /**
     * Constructor
     *
     * @param app_id  Application ID for this report
     * @param metrics What resources were used.
     */
    public ApiTransactionForAppId(String app_id, Map<String, String> metrics) {
        super(Dates.formatDate(new Date()), metrics);
        this.app_id = app_id;
    }

    @Override
    public String getId() {
        return app_id;
    }

    @Override
    public String getTransactionType() {
        return ApiTransaction.APP_ID_TRANSACTION;
    }

    public static ApiTransaction buildSingletonMetricApiTransaction(String app_id, String metricName, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap(metricName, metricValue);
        return new ApiTransactionForAppId(app_id, metrics);
    }

    public static ApiTransaction buildHitsMetricApiTransaction(String app_id, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap("hits", metricValue);
        return new ApiTransactionForAppId(app_id, metrics);
    }
}
