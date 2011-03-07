package net.threescale.api.v2;

import java.util.Date;

/**
 * DTO for metric data.
 */
public class ApiUsageMetric {

    private String metric;
    private String period;
    private String period_start;
    private String period_end;
    private String current_value;
    private String max_value;
    private Boolean exceeded;

    /**
     * Construct metric data
     * @param metric Name of metric
     * @param period Period type e.g. week, day, hour etc.
     * @param period_start  Start of period timestamp
     * @param period_end  End of period timestamp
     * @param max_value   Maximum allowed value
     * @param current_value  Currently used amount
     * @param exceeded   true if this metric has exceed its allowance.
     */
    public ApiUsageMetric(String metric, String period, String period_start, String period_end, String max_value, String current_value, String exceeded) {
        this.metric = metric;
        this.period = period;
        this.period_start = period_start;
        this.max_value = max_value;
        this.current_value = current_value;
        this.period_end = period_end;
        this.exceeded = Boolean.valueOf(exceeded);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ApiUsageMetric: [");
        builder.append("metric: \"").append(getMetric()).append("\", ");
        builder.append("period: \"").append(getPeriod()).append("\", ");
        builder.append("period_start: \"").append(getPeriodStart()).append("\", ");
        builder.append("period_end: \"").append(getPeriodEnd()).append("\", ");
        builder.append("max_value: \"").append(getMaxValue()).append("\", ");
        builder.append("current_value: \"").append(getCurrentValue()).append("\", ");
        builder.append("exceeded: ").append(getExceeded()).append(", ");
        builder.append("]");

        return builder.toString();
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

    public int margin() {
        return Integer.parseInt(getMaxValue()) - Integer.parseInt(getCurrentValue());
    }

    public boolean marginFor(int allowance) {
        return Integer.parseInt(getCurrentValue()) + allowance <= Integer.parseInt(getMaxValue());
    }

    public int getRemaining() {
        return margin();
    }

    public Date getPeriodEndDate() {
        return Dates.parseDate(getPeriodEnd());
    }


    public long getPeriodEndEpoch() {
        Date date = getPeriodStartDate();
        return date == null ? 0 : (date.getTime() + 500) / 1000L;
    }

    public Date getPeriodStartDate() {
        return Dates.parseDate(getPeriodEnd());
    }


    public long getPeriodStartEpoch() {
        Date date = getPeriodStartDate();
        return date == null ? 0 : (date.getTime() + 500) / 1000L;
    }
}
