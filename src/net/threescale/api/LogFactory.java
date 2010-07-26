package net.threescale.api;

import java.util.logging.*;

public class LogFactory {

	public static final String LOG_NAME = "net.treescale.log";
	
	public static Logger getLogger(Object clazz) {
		return Logger.getLogger(clazz.getClass().getName());
	}
}
