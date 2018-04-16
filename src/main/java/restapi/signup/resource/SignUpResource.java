package restapi.signup.resource;

import restapi.signup.model.SignUpJSON;
import restapi.signup.service.SignUpService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Tomas Perez Molina
 */

@Path("sign-up")
public class SignUpResource {
    private SignUpService service;

    public SignUpResource() {
        this.service = new SignUpService();
    }

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
