package restapi.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import model.User;
import restapi.LoginJSON;
import restapi.SimpleJSONObject;
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
