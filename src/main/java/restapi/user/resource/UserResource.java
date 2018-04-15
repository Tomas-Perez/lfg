package restapi.user.resource;

import jdk.nashorn.internal.objects.annotations.Getter;
import manager.UserManager;
import model.User;
import restapi.user.model.UserData;
import restapi.user.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * @author Tomas Perez Molina
 */

@Path("users")
public class UserResource {
    @Context
    private SecurityContext securityContext;

    private UserService service = new UserService();

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticatedUser() {

        Principal principal = securityContext.getUserPrincipal();

        User user = service.getUserByEmail(principal.getName());
        return Response.ok(new UserData(user)).build();
    }
}
