package restapi.user.service;

import manager.UserManager;
import model.User;
import restapi.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class UserService {

    @Inject
    private UserManager userManager;

    public User getUserByEmail(String email){
        return userManager.getByEmail(email).orElseThrow(AuthenticationException::noUser);
    }
}
