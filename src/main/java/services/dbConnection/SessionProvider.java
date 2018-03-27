package services.dbConnection;

import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SessionProvider {

    private static Configuration config;
    private static SessionFactory sf;
    private static Session session;

    public static Session getSession() {

        if(session==null){

            config = new Configuration();
            sf = config.configure().buildSessionFactory();
            session = sf.openSession();
        }

        return session;
    }
}
