package restapi.user.service;

import persistence.manager.UserManager;
import persistence.manager.patcher.UserPatcher;
import persistence.model.User;
import restapi.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class UserService {

    @Inject
    private UserManager manager;

    public User getUserByEmail(String email){
        return manager.getByEmail(email).orElseThrow(AuthenticationException::noUser);
    }

    public User getUser(int id){
        User user = manager.getUser(id);
        if(user == null) throw new NotFoundException();
        return user;
    }

    public void deleteUser(int id){
        try {
            manager.deleteUser(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void updateUser(int id, String username, String email, String password){
        UserPatcher patcher = new UserPatcher.Builder()
                .withUsername(username)
                .withEmail(email)
                .withPassword(password)
                .build();
        manager.updateUser(id, patcher);
    }

    public List<User> getAll(){
        return manager.listUsers();
    }
}
