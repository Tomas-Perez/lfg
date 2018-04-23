package restapi.signup.service;

import persistence.manager.ConstraintException;
import persistence.manager.UserManager;
import restapi.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class SignUpService {

    @Inject
    private UserManager userManager;

    public void signUp(String email, String password, String username){
        try {
            userManager.addUser(username, password, email, false);
        } catch (ConstraintException exc){
            throw new AuthenticationException(exc.getConstraintName() + " already exists");
        }
    }
}
