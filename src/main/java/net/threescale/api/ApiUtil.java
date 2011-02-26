package net.threescale.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ApiUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Get a Date formatter for the common API date format
     *
     * @return A date formatter with the pattern 'yyyy-mm-dd hh:mm:ss'
     */
    public static DateFormat getDataFormatter() {
        return simpleDateFormat;
    }
}
