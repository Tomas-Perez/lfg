package api.rest.signup.resource;

import api.rest.signup.model.SignUpJSON;
import api.rest.signup.service.SignUpService;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Tomas Perez Molina
 */

@Path("sign-up")
@RequestScoped
public class SignUpResource {

    @Inject
    private SignUpService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        return "SIGN UP";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response signUp(SignUpJSON signUp){
        service.signUp(
                signUp.getEmail(),
                signUp.getPassword(),
                signUp.getUsername()
        );
        return Response.status(Response.Status.CREATED).build();
    }
}
