package threescale.v3.api;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface UsageReport {
    public String getMetric();

    public String getPeriod();

    public String getCurrentValue();

    public String getMaxValue();

    public String getPeriodStart();

    public String getPeriodEnd();

    public boolean hasExceeded();
}
