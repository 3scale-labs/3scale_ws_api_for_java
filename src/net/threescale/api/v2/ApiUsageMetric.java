package net.threescale.api.v2;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 14:50:04
 */
public class ApiUsageMetric {

    private String metric;
    private String period;
    private String period_start;
    private String period_end;
    private String current_value;
    private String max_value;
    private Boolean exceeded;

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

    public Date getPeriodEndDate()  {
        return Dates.parseDate(getPeriodEnd());
    }


    public long getPeriodEndEpoch() {
        Date date = getPeriodStartDate();
        return date == null ? 0 : (date.getTime() + 500) / 1000L;
    }

    public Date getPeriodStartDate()  {
        return Dates.parseDate(getPeriodEnd());
    }


    public long getPeriodStartEpoch() {
        Date date = getPeriodStartDate();
        return date == null ? 0 : (date.getTime() + 500) / 1000L;
    }
}
