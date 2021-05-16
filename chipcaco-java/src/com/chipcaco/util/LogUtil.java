package com.chipcaco.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Return a single global logger for the application.
 * @author Rodrigo Eggea
 */
public class LogUtil {
	private static Logger logger;
	
	// Utility class, private method.
	private LogUtil() {};
	
	public static Logger getSimpleLogger() {
		if(logger == null) {
			logger = Logger.getGlobal();
			System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");
			logger.setLevel(Level.INFO);   // global log level
			ConsoleHandler handler = new ConsoleHandler();
			handler.setLevel(Level.INFO);  // handler log level
			logger.addHandler(handler);
			logger.setUseParentHandlers(false); // Avoid duplicated log messages 
			return logger;
		} else {
			return logger;
		}
	}
}
