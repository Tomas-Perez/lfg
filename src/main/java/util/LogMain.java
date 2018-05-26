package util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Tomas Perez Molina
 */

public class LogMain {
    private static Logger logger = LogManager.getLogger(LogMain.class);

    public static void main(String[] args) {
        logger.trace("trace me");
        logger.info("This is info");
        logger.error("This is an error");
        logger.debug("This is debug");
        logger.error("Exception", new RuntimeException("HOLY SHIT"));
        logger.fatal("FUCK");
    }
}
