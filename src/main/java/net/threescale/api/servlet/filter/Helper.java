package net.threescale.api.servlet.filter;

import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: Jul 25, 2011
 * Time: 11:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Helper {

    public static String processInitParam(ServletContext context,  String name, String def) {

        String tmp = context.getInitParameter(name);
        if (tmp == null) {
            tmp = def;
        }

        return tmp;
    }

}
