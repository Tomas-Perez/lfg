import services.UserManager;
import services.dbConnection.FactoryProvider;

public class Main {

    public static void main(String[] args) {


        var factory = FactoryProvider.getFactory();
        var userManager = new UserManager(factory);
//        factory.openSession();


        /* Add few User records in database */
//        var userID1 = userManager.addUser("admin", "123", "admin@lfg.com", true);
//        var userID2 = userManager.addUser("User1", "pass1", "user1@lfg.com", false);
//        var userID3 = userManager.addUser("User2", "pass2", "user2@lfg.com", false);

        /* List down all the Users */
        userManager.listUsers();

//        System.out.println("\nChanging password user 2");
//
//        userManager.updateUser(userID2, "new_password");
//
//        userManager.listUsers();
//
//        userManager.deleteUser(userID1);
//        userManager.deleteUser(userID2);
//        userManager.deleteUser(userID3);
//
//        System.out.println("\nAll users deleted");
//
//        /* Delete an User from the database */
//
//        /* List down new list of the Users */
//        userManager.listUsers();

        FactoryProvider.getFactory().close();
    }

}
