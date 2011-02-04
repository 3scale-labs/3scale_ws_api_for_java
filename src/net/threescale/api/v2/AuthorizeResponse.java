package net.threescale.api.v2;

import net.threescale.api.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Response from server containing current statistics.
 */
public class AuthorizeResponse {

    private Logger log = LogFactory.getLogger(this);

    private Boolean authorized = new Boolean(false);
    private String plan = "";
    private String reason = "";
    private ArrayList<ApiUsageMetric> usage_reports = new ArrayList<ApiUsageMetric>();


    public AuthorizeResponse(String xml) {

        try {
            log.info("Parsing response: " + xml);
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new ResponseHandler());
            parser.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
        } catch (IOException e) {
        }

    }


    public boolean getAuthorized() {
        return authorized;
    }

    public ArrayList<ApiUsageMetric> getUsageReports() {
        return usage_reports;
    }

    public String getReason() {
        return reason;
    }

    public String getPlan() {
        return plan;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthorizeResponse: [");
        builder.append("authorized: ").append(authorized).append(", ");
        builder.append("plan: \"").append(plan).append("\", ");
        builder.append("reason: \"").append(reason).append("\", ");
        builder.append("usage_reports: [");
        for (ApiUsageMetric metric : usage_reports) {
            builder.append(metric.toString());
        }
        builder.append("]");
        builder.append("]");
        return builder.toString();
    }

    /**
     * Private class that handles SAX Parsing of the response.
     */
    class ResponseHandler extends DefaultHandler {

        StringBuffer characters = new StringBuffer();

        private String metric = "";
        private String period = "";
        private String period_start = "";
        private String period_end = "";
        private String max_value = "";
        private String current_value = "";
        private String exceeded = "false";

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equalsIgnoreCase("usage_report")) {
                metric = period = period_start = period_end = max_value = current_value = "";

                metric = attributes.getValue("metric");
                period = attributes.getValue("period");

                String exceededAttribute = attributes.getValue("exceeded");
                if (exceededAttribute != null && exceededAttribute.equalsIgnoreCase("true")) {
                    exceeded = "true";
                } else {
                    exceeded = "false";
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String data = characters.toString().trim();
//            System.out.println("characters: " + data);
            if (localName.equalsIgnoreCase("authorized")) {
                authorized = new Boolean(data);

            } else if (localName.equalsIgnoreCase("plan")) {
                plan = data;
            } else if (localName.equalsIgnoreCase("reason")) {
                reason = data;
            } else if (localName.equalsIgnoreCase("period_start")) {
                period_start = data;
            } else if (localName.equalsIgnoreCase("period_end")) {
                period_end = data;
            } else if (localName.equalsIgnoreCase("current_value")) {
                current_value = data;
            } else if (localName.equalsIgnoreCase("max_value")) {
                max_value = data;
            } else if (localName.equalsIgnoreCase("usage_report")) {
                usage_reports.add(new ApiUsageMetric(metric, period, period_start, period_end, max_value, current_value, exceeded));
            }

            characters.setLength(0);
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            characters.append(chars, start, length);
        }
    }


    public int maxHits() {
        ApiUsageMetric metric = firstHitsMetric();
        return (metric != null) ? Integer.parseInt(metric.getMaxValue()) : 1;
    }

    public int currentHits() {
        ApiUsageMetric metric = firstHitsMetric();
        return (metric != null) ? Integer.parseInt(metric.getCurrentValue()) : 0;
    }


    /**
     * Returns the first hits metric. Hits metric is the default metric used by 3scale
     * @return returns the first hits metric or null, if the metric is not found
     */
    public ApiUsageMetric firstHitsMetric() {
        return firstMetricByName("hits");

    }

    /**
     * Returns the first metric which by name
     * @param metric_key name of the metric
     * @return returns the first hits metric or null, if the metric is not found
     */
    public ApiUsageMetric firstMetricByName(String metric_key) {

        for (ApiUsageMetric metric : usage_reports) {
            if (metric.getMetric().equals(metric_key)) {
                return metric;
            }
        }
        return null;
    }

    // Find a specific metric/period usage metric
    public ApiUsageMetric findMetricForPeriod(String metric_key, String period_key) {
        for (ApiUsageMetric metric : usage_reports) {
            if (metric.getMetric().equals(metric_key) && metric.getPeriod().equals(period_key)) {
                return metric;
            }
        }
        return null;
    }

}
