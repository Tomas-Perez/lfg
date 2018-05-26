package api.rest.security.authentication.resource;

import api.rest.security.authentication.model.AuthenticationToken;
import api.rest.security.authentication.model.SignInJSON;
import api.rest.security.authentication.service.AuthenticationTokenService;
import api.rest.security.authentication.service.SignInService;

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
        service.signIn(signIn.getEmail(), signIn.getPassword());
        String token = authenticationTokenService.issueToken(signIn.getEmail());

        return Response.ok(new AuthenticationToken(token)).build();
    }
}
