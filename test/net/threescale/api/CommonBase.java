package net.threescale.api;

import net.threescale.api.v2.*;
import net.threescale.api.v2.HttpSender;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Oct-2010
 * Time: 12:55:28
 */
public class CommonBase {

    protected static final String SERVER_URL = "su1.3scale.net";
    protected static final String APP_ID = "api-id-ffff";
    protected static final String APP_KEY =   "3scale-dsfsdfdsfisodfsdf491e6d941b5b522";
    protected static final String PROVIDER_KEY = "goodf621b66acb7ec8ceabed4b7aff278";
    protected static final String REFERRER_IP = "123.456.789.001";



    protected static final String HAPPY_PATH_RESPONSE =
        "<status>" +
        "  <authorized>true</authorized>" +
        "  <plan>Basic</plan>" +

        "  <usage_reports>" +
        "    <usage_report metric=\"hits\" period=\"month\">" +
        "      <period_start>2010-08-01 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-09-01 00:00:00 +00:00</period_end>" +
        "      <current_value>17344</current_value>" +
        "      <max_value>20000</max_value>" +
        "    </usage_report>" +
        "    <usage_report metric=\"hits\" period=\"day\">" +
        "      <period_start>2010-08-04 00:00:00 +00:00</period_start>" +
        "      <period_end>2010-08-05 00:00:00 +00:00</period_end>" +
        "      <current_value>732</current_value>" +
        "      <max_value>1000</max_value>" +
        "    </usage_report>" +
        "  </usage_reports>" +
        "</status>";
    
}
