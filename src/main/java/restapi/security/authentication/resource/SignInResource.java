package restapi.security.authentication.resource;

import persistence.model.User;
import restapi.security.authentication.model.AuthenticationToken;
import restapi.security.authentication.model.SignInJSON;
import restapi.security.authentication.service.AuthenticationTokenService;
import restapi.security.authentication.service.SignInService;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Tomas Perez Molina
 */

@Path("sign-in")
@RequestScoped
public class SignInResource {

    @Inject
    private SignInService service;

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    public SignInResource() {
        System.out.println("Sign-in resource created");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "SIGN IN";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response singIn(SignInJSON signIn){
        User user = service.signIn(signIn.getEmail(), signIn.getPassword());
        String token = authenticationTokenService.issueToken(user.getEmail());

        return Response.ok(new AuthenticationToken(token)).build();
    }
}
