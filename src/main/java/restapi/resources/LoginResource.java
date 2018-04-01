package restapi.resources;

import restapi.pojos.LoginJSON;
import restapi.pojos.SimpleJSONObject;
import restapi.services.LoginService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Tomas Perez Molina
 */

@Path("login")
public class LoginResource {
    private LoginService service;

    public LoginResource() {
        this.service = new LoginService();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "LOGIN";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SimpleJSONObject login(LoginJSON login){
        return service.login(login.getEmail(), login.getPassword());
    }
}
