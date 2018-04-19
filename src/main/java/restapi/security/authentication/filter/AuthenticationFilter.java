package restapi.security.authentication.filter;

import manager.UserManager;
import model.User;
import restapi.security.authentication.exception.AuthenticationException;
import restapi.security.authentication.service.AuthenticationTokenDetails;
import restapi.security.authentication.service.AuthenticationTokenService;
import restapi.security.common.filter.AuthenticatedUserDetails;
import restapi.security.common.filter.TokenBasedSecurityContext;
import restapi.security.common.domain.Authority;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@Dependent
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    private UserManager userManager;



    @Override
    public void filter(ContainerRequestContext requestContext){
        System.out.println("Authentication");
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (isTokenBasedAuthentication(authorizationHeader)) {
            String token = authorizationHeader
                    .substring(AUTHENTICATION_SCHEME.length())
                    .trim();
            try {
                validateToken(token, requestContext);
            } catch (AuthenticationException e) {
                abortWithUnauthorized(requestContext);
            }
            return;
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

    private void validateToken(String token, ContainerRequestContext requestContext){
        AuthenticationTokenDetails tokenDetails = authenticationTokenService.parseToken(token);

        User user = userManager.getByEmail(tokenDetails.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid token"));

        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(
                user.getEmail(),
                Authority.getAuthoritySet(user.isAdmin())
        );

        boolean isSecure = requestContext.getSecurityContext().isSecure();
        SecurityContext securityContext = new TokenBasedSecurityContext(userDetails, tokenDetails, isSecure);
        requestContext.setSecurityContext(securityContext);
    }
}
