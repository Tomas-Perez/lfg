package restapi.user.service;

import persistence.entity.UserEntity;
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

    public int getIDByEmail(String email){
        UserEntity userEntity = manager.getByEmail(email)
                .orElseThrow(AuthenticationException::noUser);
        return userEntity.getId();
    }

    public User getUser(int id){
        try {
            return modelBuilder.buildUser(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void deleteUser(int id){
        try {
            manager.delete(id);
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
        return manager.list()
                .stream()
                .map(modelBuilder::buildUser)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(int id){
        return manager.getUserFriends(id)
                .stream()
                .map(modelBuilder::buildUser)
                .collect(Collectors.toList());
    }

    public List<User> getReceivedRequests(int id){
        return manager.getReceivedRequests(id)
                .stream()
                .map(modelBuilder::buildUser)
                .collect(Collectors.toList());
    }

    public List<User> getSentRequests(int id){
        return manager.getSentRequests(id)
                .stream()
                .map(modelBuilder::buildUser)
                .collect(Collectors.toList());
    }

    public void sendFriendRequest(int id1, int id2){
        manager.addFriendRequest(id1, id2);
    }

    public void confirmFriendRequest(int id1, int id2){
        manager.confirmFriend(id1, id2);
    }

    public void removeFriend(int id1, int id2){
        manager.removeFriend(id1, id2);
    }
}
