package restapi.signup.service;

import manager.ConstraintException;
import manager.UserManager;
import manager.FactoryProvider;
import restapi.security.authentication.exception.AuthenticationException;

/**
 * @author Tomas Perez Molina
 */
public class SignUpService {
    private UserManager userManager;

    public SignUpService() {
        this.userManager = new UserManager(FactoryProvider.getFactory());
    }

    public void signUp(String email, String password, String username){
        try {
            userManager.addUser(username, password, email, false);
        } catch (ConstraintException exc){
            throw new AuthenticationException(exc.getConstraintName() + " already exists");
        }
    }
}
