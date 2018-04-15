package restapi.security.authentication.service;

import model.User;
import manager.UserManager;
import manager.FactoryProvider;
import restapi.security.authentication.exception.AuthenticationException;

/**
 * @author Tomas Perez Molina
 */
public class SignInService {
    private UserManager userManager;

    public SignInService() {
        this.userManager = new UserManager(FactoryProvider.getFactory());
    }

    public User signIn(String email, String password){
        User user;

        user = userManager.getByEmail(email).orElseThrow(AuthenticationException::noUser);

        if(!password.equals(user.getPassword()))
            throw new AuthenticationException("Wrong password");

        return user;
    }
}
