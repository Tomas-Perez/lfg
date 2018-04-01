package managers;

import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class FactoryProvider {

    private static SessionFactory sf;

    public static SessionFactory getFactory() {
        if(sf == null){
            try {
                sf = new Configuration().configure().buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }

        return sf;
    }
}
