package net.threescale.api;

public class CommonBase {

    protected static final String SERVER_URL = "su1.3scale.net";
    protected static final String APP_ID = "api-id-ffff";
    protected static final String APP_KEY = "3scale-dsfsdfdsfisodfsdf491e6d941b5b522";
    protected static final String USER_KEY = "3scale-dsdfdsfioio89123021s121222";
    protected static final String PROVIDER_KEY = "goodf621b66acb7ec8ceabed4b7aff278";
    protected static final String REFERRER = "example.org";


    protected static final String HAPPY_PATH_RESPONSE =
            "<status>" +
                    "  <authorized>true</authorized>" +
                    "  <plan>Basic</plan>" +

                    "  <usage_reports>" +
                    "    <usage_report metric=\"hits\" period=\"month\">" +
                    "      <period_start>2010-08-01 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-09-01 00:00:00 +0000</period_end>" +
                    "      <current_value>17344</current_value>" +
                    "      <max_value>20000</max_value>" +
                    "    </usage_report>" +
                    "    <usage_report metric=\"hits\" period=\"day\">" +
                    "      <period_start>2010-08-04 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-08-05 00:00:00 +0000</period_end>" +
                    "      <current_value>732</current_value>" +
                    "      <max_value>1000</max_value>" +
                    "    </usage_report>" +
                    "  </usage_reports>" +
                    "</status>";

    protected static final String HAPPY_PATH_RESPONSE2 =
            "<status>" +
                    "  <authorized>true</authorized>" +
                    "  <plan>Pro</plan>" +

                    "  <usage_reports>" +
                    "    <usage_report metric=\"hits\" period=\"month\">" +
                    "      <period_start>2010-08-01 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-09-01 00:00:00 +0000</period_end>" +
                    "      <current_value>10000</current_value>" +
                    "      <max_value>20000</max_value>" +
                    "    </usage_report>" +
                    "    <usage_report metric=\"hits\" period=\"day\">" +
                    "      <period_start>2010-08-04 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-08-05 00:00:00 +0000</period_end>" +
                    "      <current_value>732</current_value>" +
                    "      <max_value>1000</max_value>" +
                    "    </usage_report>" +
                    "  </usage_reports>" +
                    "</status>";

    protected static final String LIMITS_EXCEEDED_RESPONSE =
            "<status>" +
                    "  <authorized>false</authorized>" +
                    "  <plan>Basic</plan>" +

                    "  <usage_reports>" +
                    "    <usage_report metric=\"hits\" period=\"month\">" +
                    "      <period_start>2010-08-01 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-09-01 00:00:00 +0000</period_end>" +
                    "      <current_value>17344</current_value>" +
                    "      <max_value>20000</max_value>" +
                    "    </usage_report>" +
                    "    <usage_report metric=\"hits\" period=\"day\" exceeded=\"true\">" +
                    "      <period_start>2010-08-04 00:00:00 +0000</period_start>" +
                    "      <period_end>2010-08-05 00:00:00 +0000</period_end>" +
                    "      <current_value>1240</current_value>" +
                    "      <max_value>1000</max_value>" +
                    "    </usage_report>" +
                    "  </usage_reports>" +
                    "</status>";
    //yyyy-MM-dd HH:mm:ss Z

    protected static final String RESPONSE_HAPPY_PATH_DATA_WITH_APP_ID =
            "provider_key=" + PROVIDER_KEY + "&" +
                    "transactions[0][app_id]=bce4c8f4&" +
                    "transactions[0][usage][transfer]=4500&" +
                    "transactions[0][usage][hits]=1&" +
                    "transactions[0][timestamp]=2009-01-01+14%3A23%3A08&" +
                    "transactions[1][app_id]=bad7e480&" +
                    "transactions[1][usage][transfer]=2840&" +
                    "transactions[1][usage][hits]=1&" +
                    "transactions[1][timestamp]=2009-01-01+18%3A11%3A59";

    protected static final String RESPONSE_HAPPY_PATH_DATA_WITH_USER_KEY =
            "provider_key=" + PROVIDER_KEY + "&" +
                    "transactions[0][user_key]=asdfsdf&" +
                    "transactions[0][usage][transfer]=4500&" +
                    "transactions[0][usage][hits]=1&" +
                    "transactions[0][timestamp]=2009-01-01+14%3A23%3A08&" +
                    "transactions[1][user_key]=dsssddd&" +
                    "transactions[1][usage][transfer]=2840&" +
                    "transactions[1][usage][hits]=1&" +
                    "transactions[1][timestamp]=2009-01-01+18%3A11%3A59";

    protected static final String INVALID_APP_ID_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
        "  <error code=\"application_not_found\">Application with id=\"12345678\" was not found</error>";

}
