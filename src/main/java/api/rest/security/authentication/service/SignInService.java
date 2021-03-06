package api.rest.security.authentication.service;

import persistence.entity.UserEntity;
import persistence.manager.UserManager;
import api.rest.security.authentication.exception.AuthenticationException;

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

    public void signIn(String email, String password){
        UserEntity user;

        user = userManager.getByEmail(email).orElseThrow(AuthenticationException::noUser);

        if(!password.equals(user.getPassword()))
            throw new AuthenticationException("Wrong password");
    }
}
