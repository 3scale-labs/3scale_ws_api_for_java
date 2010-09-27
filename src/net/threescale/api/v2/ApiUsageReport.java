package net.threescale.api.v2;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 14:50:04
 */
public class ApiUsageReport {

    private String metric;
    private String period;
    private String period_start;
    private String period_end;
    private String current_value;
    private String max_value;
    private Boolean exceeded;

    public ApiUsageReport(String metric, String period, String period_start, String period_end, String max_value, String current_value, String exceeded) {
        this.metric = metric;
        this.period = period;
        this.period_start = period_start;
        this.max_value = max_value;
        this.current_value = current_value;
        this.period_end = period_end;
        this.exceeded = Boolean.valueOf(exceeded);
    }

    public String getMetric() {
        return metric;
    }

    public String getPeriod() {
        return period;
    }

    public String getPeriodStart() {
        return period_start;
    }

    public String getPeriodEnd() {
        return period_end;
    }

    public String getCurrentValue() {
        return current_value;
    }

    public String getMaxValue() {
        return max_value;
    }

    public Boolean getExceeded() {
        return exceeded;
    }
}
