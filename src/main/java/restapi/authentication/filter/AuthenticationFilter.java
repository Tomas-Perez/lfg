package restapi.authentication.filter;

import manager.FactoryProvider;
import manager.UserManager;
import model.User;
import restapi.authentication.exception.AuthenticationException;
import restapi.authentication.service.AuthenticationTokenDetails;
import restapi.authentication.service.AuthenticationTokenService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    private AuthenticationTokenService authenticationTokenService = new AuthenticationTokenService();
    private UserManager userManager = new UserManager(FactoryProvider.getFactory());

    @Override
    public void filter(ContainerRequestContext requestContext){
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            System.out.println("No token");
            return;
        }

        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length())
                .trim();

        try {
            validateToken(token);
        } catch (AuthenticationException e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken(String token){
        AuthenticationTokenDetails authenticationTokenDetails = authenticationTokenService.parseToken(token);
        User user = userManager.getByEmail(authenticationTokenDetails.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid token"));
    }
}
