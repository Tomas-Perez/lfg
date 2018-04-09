package restapi.resources;

import restapi.pojos.SignInJSON;
import restapi.pojos.SimpleJSONObject;
import restapi.services.SignInService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Tomas Perez Molina
 */

@Path("sign-in")
public class SignInResource {
    private SignInService service;

    public SignInResource() {
        this.service = new SignInService();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "SIGN IN";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimpleJSONObject singIn(SignInJSON signIn){
        return service.signIn(signIn.getEmail(), signIn.getPassword());
    }
}
