package restapi.signup.service;

import persistence.entity.UserEntity;
import persistence.manager.exception.ConstraintException;
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
        UserEntity user = new UserEntity(false, email, password, username);
        try {
            userManager.add(user);
        } catch (ConstraintException exc){
            throw new AuthenticationException(exc.getConstraintName() + " already exists");
        }
    }
}
