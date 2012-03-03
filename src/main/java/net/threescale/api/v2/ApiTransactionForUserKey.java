package net.threescale.api.v2;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class ApiTransactionForUserKey extends ApiTransaction {

    private String user_key;
    /**
     * Constructor
     *
     * @param user_key  User Key for this report
     * @param timestamp When the transaction took place
     * @param metrics   What resources were used.
     */
    public ApiTransactionForUserKey(String user_key, String timestamp, Map<String, String> metrics) {
        super(timestamp, metrics);
        this.user_key = user_key;
   }

    /**
     * Constructor
     *
     * @param user_key  User Key for this report
     * @param metrics What resources were used.
     */
    public ApiTransactionForUserKey(String user_key, Map<String, String> metrics) {
        super(Dates.formatDate(new Date()), metrics);
        this.user_key = user_key;
    }

    @Override
    public String getId() {
        return user_key;
    }

    @Override
    public String getTransactionType() {
        return ApiTransaction.USER_KEY_TRANSACTION;
    }

    public static ApiTransaction buildSingletonMetricApiTransaction(String app_id, String metricName, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap(metricName, metricValue);
        return new ApiTransactionForUserKey(app_id, metrics);
    }

    public static ApiTransaction buildHitsMetricApiTransaction(String app_id, String metricValue) {
        Map<String, String> metrics = Collections.singletonMap("hits", metricValue);
        return new ApiTransactionForUserKey(app_id, metrics);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        ApiTransactionForUserKey second = (ApiTransactionForUserKey) o;
        if (super.equals(o) && (this.user_key == second.user_key))
            return true;
        else
            return false;
    }
}
