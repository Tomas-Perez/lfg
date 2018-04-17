package restapi.test.resource;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Tomas Perez Molina
 */
@Path("test")
@RequestScoped
public class TestResource {

    @GET
    @Path("admin")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.TEXT_PLAIN)
    public String helloAdmin(){
        return "YOU ARE AN ADMIN";
    }

    @GET
    @Path("user")
    @RolesAllowed({"USER"})
    @Produces(MediaType.TEXT_PLAIN)
    public String helloUser(){
        return "YOU ARE A USER";
    }
}
