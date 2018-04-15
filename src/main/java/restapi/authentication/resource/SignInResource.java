package restapi.authentication.resource;

import model.User;
import restapi.authentication.filter.Secured;
import restapi.authentication.model.AuthenticationToken;
import restapi.authentication.model.SignInJSON;
import restapi.authentication.service.AuthenticationTokenService;
import restapi.authentication.service.SignInService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Tomas Perez Molina
 */

@Path("sign-in")
public class SignInResource {

    private SignInService service = new SignInService();

    private AuthenticationTokenService authenticationTokenService = new AuthenticationTokenService();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Secured
    public String getIt() {
        return "SIGN IN";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response singIn(SignInJSON signIn){
        User user = service.signIn(signIn.getEmail(), signIn.getPassword());
        String token = authenticationTokenService.issueToken(user.getEmail());

        return Response.ok(new AuthenticationToken(token)).build();
    }
}
