package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author Tomas Perez Molina
 */
public class LogTest {
    private static Logger logger = LogManager.getLogger(LogTest.class);

    @Test
    public void test(){
        logger.trace("trace me");
        logger.info("This is info");
        logger.error("This is an error");
        logger.debug("This is debug");
        logger.error("Exception", new RuntimeException("HOLY SHIT"));
        logger.fatal("FUCK");
    }

}
