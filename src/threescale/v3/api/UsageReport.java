package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface UsageReport {
    public String getMetric();

    public Object getPeriod();

    public String getCurrentValue();

    public String getMaxValue();

    public Object periodStart();

    public Object periodEnd();

    public boolean hasExceeded();
}
