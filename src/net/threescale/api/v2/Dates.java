package net.threescale.api.v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: cpatni
 * Date: Feb 3, 2011
 * Time: 8:39:13 AM
 */
public class Dates {
    private static final String YYYY_MM_DD_HH_MM_SS_Z = "yyyy-MM-dd HH:mm:ss Z";

    public static Date parseDate(String date) {
        return parseDate(date, null);
    }

    public static Date parseDate(String date, Date defaultValue) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_Z);
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_Z);
        return dateFormatter.format(date);
    }

}
