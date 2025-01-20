package com.geocode.search.logging;

import org.slf4j.LoggerFactory;

public class Logger {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    /**
     * Method used to print error messages
     * @param alert alert message
     * @param message error description
     */
    public static void printError(String alert, String message) {
        logger.error("{} Message: {}", alert, message);
    }

    /**
     * Method used to print error messages
     * @param alert alert message
     */
    public static void printError(String alert) {
        logger.error(alert);
    }

    /**
     * Method used to print error messages
     * @param message message to show
     */
    public static void printInfo(String message) {
        logger.info(message);
    }
}
