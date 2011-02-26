package net.threescale.api;

import java.util.logging.Logger;

public class LogFactory {

    public static Logger getLogger(Object clazz) {
        return Logger.getLogger(clazz.getClass().getName());
    }
}
