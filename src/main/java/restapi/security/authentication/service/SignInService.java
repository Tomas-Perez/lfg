package restapi.security.authentication.service;

import manager.UserManager;
import model.User;
import restapi.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class SignInService {

    @Inject
    private UserManager userManager;

    public SignInService() {
        System.out.println("Initiating sign in service");
    }

    public User signIn(String email, String password){
        User user;

        user = userManager.getByEmail(email).orElseThrow(AuthenticationException::noUser);

        if(!password.equals(user.getPassword()))
            throw new AuthenticationException("Wrong password");

        return user;
    }
}
