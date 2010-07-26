package net.threescale.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Jul-2010
 * Time: 10:36:59
 */
public class ApiUsage {
    
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private Logger log = LogFactory.getLogger(this);

    private final String metric;
    private final String period;
    private final Date periodStart;
    private final Date periodEnd;
    private final String currentValue;
    private final String maxValue;

    public ApiUsage(String metric, String period, String periodStart, String periodEnd, String currentValue, String maxValue) {
        this.metric = metric;
        this.period = period;
        this.periodStart = stringToDate(periodStart);
        this.periodEnd = stringToDate(periodEnd);
        this.currentValue = currentValue;
        this.maxValue = maxValue;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    private Date stringToDate(String value) {
        try {
            return dateFormatter.parse(value);
        } catch (ParseException e) {
            log.log(Level.WARNING, e.getMessage(), e);
            return new Date();
        }
    }

    public String getMetric() {
        return metric;
    }

    public String getPeriod() {
        return period;
    }
}
