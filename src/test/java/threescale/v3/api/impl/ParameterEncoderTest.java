package threescale.v3.api.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import threescale.v3.api.ParameterMap;

import static org.junit.Assert.assertEquals;

/**
 * JUnit Test class
 */
public class ParameterEncoderTest {

    private ParameterEncoder encoder;
    DateTimeFormatter fmt;

    @Before
    public void setup() {
        encoder = new ParameterEncoder();
        fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss Z");
    }

    @Test
    public void testEncodeOneParameter() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");

        assertEquals("provider_key=123abc", encoder.encode(param));
    }

    @Test
    public void testEncodeTwoParameters() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");
        param.add("app_id", "3456aaa");

        assertEquals("provider_key=123abc&app_id=3456aaa", encoder.encode(param));
    }

    @Test
    public void testEncodeTwoParametersAndOneMap() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");
        param.add("app_id", "3456aaa");

        ParameterMap usage = new ParameterMap();
        usage.add("hits", "111");
        param.add("usage", usage);

        assertEquals("provider_key=123abc&app_id=3456aaa&usage%5Bhits%5D=111", encoder.encode(param));
    }

    @Test
    public void testEncodeTwoParametersAndTwoMap() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");
        param.add("app_id", "3456aaa");

        ParameterMap usage = new ParameterMap();
        usage.add("hits", "111");
        usage.add("timestamp", fmt.print(new DateTime(2010, 4, 27, 15, 0, DateTimeZone.UTC)));
        param.add("usage", usage);


        assertEquals(
                "provider_key=123abc&app_id=3456aaa&usage%5Bhits%5D=111&usage%5Btimestamp%5D=2010-04-27%2015%3A00%3A00%20+0000",
                encoder.encode(param));
    }

    @Test
    public void testEncodingAnArray() throws Exception {
        final String expected =
                  "provider_key=1234abcd&"
                + "transactions%5B0%5D%5Bapp_id%5D=foo&"
                + "transactions%5B0%5D%5Btimestamp%5D=2010-04-27%2015%3A42%3A17%200200&"
                + "transactions%5B0%5D%5Busage%5D%5Bhits%5D=1&"
                + "transactions%5B1%5D%5Bapp_id%5D=bar&"
                + "transactions%5B1%5D%5Btimestamp%5D=2010-04-27%2015%3A55%3A12%200200&"
                + "transactions%5B1%5D%5Busage%5D%5Bhits%5D=1";

        ParameterMap app1 = new ParameterMap();
        app1.add("app_id", "foo");
        app1.add("timestamp", "2010-04-27 15:42:17 0200");

        ParameterMap usage1 = new ParameterMap();
        usage1.add("hits", "1");
        app1.add("usage", usage1);

        ParameterMap app2 = new ParameterMap();
        app2.add("app_id", "bar");
        app2.add("timestamp", "2010-04-27 15:55:12 0200");

        ParameterMap usage2 = new ParameterMap();
        usage2.add("hits", "1");
        app2.add("usage", usage2);

        ParameterMap[] transactions = new ParameterMap[2];
        transactions[0] = app1;
        transactions[1] = app2;

        ParameterMap params = new ParameterMap();
        params.add("provider_key", "1234abcd");
        params.add("transactions", transactions);

        assertEquals(expected, encoder.encode(params));

    }
}
