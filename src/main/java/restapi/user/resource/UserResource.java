package restapi.user.resource;

import persistence.model.User;
import restapi.user.model.UserData;
import restapi.user.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
@RequestScoped
public class UserResource {
    @Context
    private SecurityContext securityContext;

    @Inject
    private UserService service;

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticatedUser() {
        Principal principal = securityContext.getUserPrincipal();
        System.out.println(principal.getName());
        User user = service.getUserByEmail(principal.getName());
        return Response.ok(new UserData(user)).build();
    }
}
