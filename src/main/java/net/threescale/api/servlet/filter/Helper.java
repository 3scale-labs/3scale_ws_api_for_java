package net.threescale.api.servlet.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: Jul 25, 2011
 * Time: 11:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Helper {

    public static String processInitParam(ServletConfig config,  String name, String def) {

        String tmp = config.getInitParameter(name);
        if (tmp == null) {
            tmp = def;
        }

        return tmp;
    }

    public static String processInitParam(FilterConfig filterConfig,  String name, String def) {

        String tmp = filterConfig.getInitParameter(name);
        if (tmp == null) {
            tmp = def;
        }

        return tmp;
    }

}
