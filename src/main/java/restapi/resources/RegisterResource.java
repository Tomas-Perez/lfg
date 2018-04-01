package restapi.resources;

import restapi.pojos.RegisterJSON;
import restapi.pojos.SimpleJSONObject;
import restapi.services.RegisterService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Tomas Perez Molina
 */

@Path("register")
public class RegisterResource {
    private RegisterService service;

    public RegisterResource() {
        this.service = new RegisterService();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        return "REGISTER";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimpleJSONObject register(RegisterJSON register){
        return service.register(
                register.getEmail(),
                register.getPassword(),
                register.getUsername()
        );
    }
}
