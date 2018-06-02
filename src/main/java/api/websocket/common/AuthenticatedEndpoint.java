package api.websocket.common;

import api.rest.security.authentication.exception.AuthenticationException;
import api.websocket.common.security.AuthenticatedPrincipal;

import javax.websocket.Session;
import java.security.Principal;

/**
 * @author Tomas Perez Molina
 */
public abstract class AuthenticatedEndpoint {
    protected int getUserID(Session session){
        return getPrincipal(session).getId();
    }

    protected AuthenticatedPrincipal getPrincipal(Session session){
        Principal principal = session.getUserPrincipal();
        if(principal instanceof AuthenticatedPrincipal){
            return ((AuthenticatedPrincipal) session.getUserPrincipal());
        } else {
            throw new AuthenticationException("User is not authenticated.");
        }
    }
}
