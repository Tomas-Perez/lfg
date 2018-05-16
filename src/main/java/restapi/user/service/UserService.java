package restapi.user.service;

import model.UserEntity;
import persistence.manager.UserManager;
import persistence.manager.patcher.UserPatcher;
import persistence.model.ModelBuilder;
import persistence.model.User;
import restapi.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class UserService {

    @Inject
    private UserManager manager;

    @Inject
    private ModelBuilder modelBuilder;

    public User getUserByEmail(String email){
        UserEntity userEntity = manager.getByEmail(email)
                .orElseThrow(AuthenticationException::noUser);
        return getUser(userEntity.getId());
    }

    public User getUser(int id){
        return modelBuilder.buildUser(id);
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
        return manager.listUsers()
                .stream()
                .map(modelBuilder::buildUser)
                .collect(Collectors.toList());
    }
}
