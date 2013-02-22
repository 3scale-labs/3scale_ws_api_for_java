package threescale.v3.api.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import threescale.v3.api.ParameterMap;

/**
 * User: geoffd
 * Date: 21/02/2013
 */
public class ClientDriverTest {

    private ClientDriver cd;
    DateTimeFormatter fmt;

    @Before
    public void setup() {
        cd = new ClientDriver(null);
        fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss Z");
    }

    @Test
    public void testEncodeOneParameter() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");

        Assert.assertEquals("provider_key=123abc", cd.encodeAsString(param, null));
    }

    @Test
    public void testEncodeTwoParameters() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");
        param.add("app_id", "3456aaa");

        Assert.assertEquals("provider_key=123abc&app_id=3456aaa", cd.encodeAsString(param, null));
    }

    @Test
    public void testEncodeTwoParametersAndOneMap() throws Exception {
        ParameterMap param = new ParameterMap();
        param.add("provider_key", "123abc");
        param.add("app_id", "3456aaa");

        ParameterMap usage = new ParameterMap();
        usage.add("hits", "111");
        param.add("usage", usage);

        Assert.assertEquals("provider_key=123abc&[usage][hits]=111&app_id=3456aaa", cd.encodeAsString(param, null));
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


        Assert.assertEquals(
                "provider_key=123abc&[usage][timestamp]=2010-04-27 15:00:00 +0000&[usage][hits]=111&app_id=3456aaa",
                cd.encodeAsString(param, null));
    }

}
