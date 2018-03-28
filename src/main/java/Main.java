import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import services.UserManager;

public class Main {


    private static SessionFactory factory;

    public static void main(String[] args) {

        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        var userManager = new UserManager(factory);


        /* Add few User records in database */
        var userID1 = userManager.addUser("admin", "123", "admin@lfg.com", true);
        var userID2 = userManager.addUser("User1", "pass1", "user1@lfg.com", false);
        var userID3 = userManager.addUser("User2", "pass2", "user2@lfg.com", false);

        /* List down all the Users */
        userManager.listUsers();

        /* Update User's records */
        //ME.updateUser(userID1, "PASS1");

        /* Delete an User from the database */

        /* List down new list of the Users */
        userManager.listUsers();

        factory.close();
    }

}
