package dbConnection;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Tomas Perez Molina
 */
public class DBInit {
    public static void main(String[] args) throws Exception{
        init();
    }

    public static void init() throws Exception {
        try {
            System.out.println("Starting Database");
            HsqlProperties p = new HsqlProperties();
            p.setProperty("server.database.0", "file:db/demodb");
            p.setProperty("server.dbname.0", "testdb");
            Server server = new Server();
            server.setProperties(p);
            server.setLogWriter(new PrintWriter(System.out));
            server.setErrWriter(new PrintWriter(System.out));
            server.start();
        } catch (ServerAcl.AclFormatException | IOException afex) {
            throw new Exception(afex);
        }
    }
}
