package restapi.user.service;

import manager.FactoryProvider;
import manager.UserManager;
import model.User;
import restapi.security.authentication.exception.AuthenticationException;

/**
 * @author Tomas Perez Molina
 */
public class UserService {
    private UserManager userManager = new UserManager(FactoryProvider.getFactory());

    public User getUserByEmail(String email){
        return userManager.getByEmail(email).orElseThrow(AuthenticationException::noUser);
    }
}
