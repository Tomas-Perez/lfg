package manager;

import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class FactoryProvider {

    private static SessionFactory sf;

    public static void createFactory(){
        try {
            sf = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getFactory() {
        if(sf == null){
            createFactory();
        }
        return sf;
    }
}
