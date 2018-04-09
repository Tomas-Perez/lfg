package restapi.resources;

import restapi.pojos.SignUpJSON;
import restapi.pojos.SimpleJSONObject;
import restapi.services.SignUpService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public SimpleJSONObject signUp(SignUpJSON signUp){
        return service.signUp(
                signUp.getEmail(),
                signUp.getPassword(),
                signUp.getUsername()
        );
    }
}
