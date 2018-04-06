package utils;

import managers.UserManager;
import org.hibernate.SessionFactory;
import managers.FactoryProvider;

public class Main {

    public static void main(String[] args) {


        SessionFactory factory = FactoryProvider.getFactory();
        UserManager userManager = new UserManager(factory);
        factory.openSession();


        /* Add few User records in database */
//        Integer userID1 = userManager.addUser("admin", "123", "admin@lfg.com", true);
//        Integer userID2 = userManager.addUser("User1", "pass1", "user1@lfg.com", false);
//        Integer userID3 = userManager.addUser("User2", "pass2", "user2@lfg.com", false);

        /* List down all the Users */
        userManager.listUsers();

//        System.out.println(userManager.getByUsername("User1"));
////        System.out.println("\nChanging password user 2");
////
////        userManager.updateUser(userID2, "new_password");
////
////        userManager.listUsers();
//
////        userManager.deleteUser(userID1);
////        userManager.deleteUser(userID2);
////        userManager.deleteUser(userID3);
////
////        System.out.println("\nAll users deleted");
//
//        /* Delete an User from the database */
//
//        /* List down new list of the Users */
//        userManager.listUsers();

        FactoryProvider.getFactory().close();
    }

}
