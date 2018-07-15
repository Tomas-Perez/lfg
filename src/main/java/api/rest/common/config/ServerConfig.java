package api.rest.common.config;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * @author Tomas Perez Molina
 */
@ApplicationPath("/*")
public class ServerConfig extends ResourceConfig {

    public ServerConfig() {
        register(MultiPartFeature.class);
        packages("api.rest");
        System.out.println("CONFIGURED");
    }
}
