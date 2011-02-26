package net.threescale.api;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DTO for API Usage Metrics
 */
public class ApiUsageMetric {

    private Logger log = LogFactory.getLogger(this);

    private final String metric;
    private final String period;
    private final Date periodStart;
    private final Date periodEnd;
    private final String currentValue;
    private final String maxValue;

    public ApiUsageMetric(String metric, String period, String periodStart, String periodEnd, String currentValue, String maxValue) {
        this.metric = metric;
        this.period = period;
        this.periodStart = stringToDate(periodStart);
        this.periodEnd = stringToDate(periodEnd);
        this.currentValue = currentValue;
        this.maxValue = maxValue;
    }

    /**
     * @return The period start date and time for this usage
     */
    public Date getPeriodStart() {
        return periodStart;
    }

    /**
     * @return The period end date and time for this usage
     */
    public Date getPeriodEnd() {
        return periodEnd;
    }

    /**
     * @return The current value for this usage
     */
    public String getCurrentValue() {
        return currentValue;
    }

    /**
     * @return The maximum value for this usage
     */
    public String getMaxValue() {
        return maxValue;
    }

    private Date stringToDate(String value) {
        try {
            return ApiUtil.getDataFormatter().parse(value);
        } catch (ParseException e) {
            log.log(Level.WARNING, e.getMessage(), e);
            return new Date();
        }
    }


    /**
     * @return The name of this metric
     */
    public String getMetric() {
        return metric;
    }

    /**
     * @return The period for this metric
     */
    public String getPeriod() {
        return period;
    }
}
