package util.dbconnection;

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
        init(Database.TEST2);
    }

    public static void init(Database db) throws Exception {
        try {
            System.out.println("Starting Database");
            HsqlProperties p = new HsqlProperties();
            p.setProperty("server.database.0", "file:" + db.path);
            p.setProperty("server.dbname.0", db.name);
            Server server = new Server();
            server.setProperties(p);
            server.setLogWriter(new PrintWriter(System.out));
            server.setErrWriter(new PrintWriter(System.out));
            server.start();
        } catch (ServerAcl.AclFormatException | IOException afex) {
            throw new Exception(afex);
        }
    }

    private enum Database {
        PROD("db/schemas/prod", "prod"), TEST("db/schemas/test", "test"), TEST2("db/schemas/test2", "test2");

        private String path;
        private String name;

        private Database(String path, String name){
            this.path = path;
            this.name = name;
        }
    }
}
