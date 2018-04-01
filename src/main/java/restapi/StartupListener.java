package restapi;

import managers.FactoryProvider;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author Tomas Perez Molina
 */

@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("INIT!");
        FactoryProvider.getFactory().openSession();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("BYE!");
        FactoryProvider.getFactory().close();
    }
}
