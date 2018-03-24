package hello;

/**
 * @author Tomas Perez Molina
 */
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {
    public static void main(String[] args) {
        Connection con;

        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/testdb", "SA", "password");
            if (con!= null)
                System.out.println("Connection created successfully");
            else
                System.out.println("Problem with creating connection");
        }  catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
