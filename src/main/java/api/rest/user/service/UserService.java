package api.rest.user.service;

import api.common.event.friendrequest.*;
import api.rest.user.model.BasicUserData;
import persistence.entity.UserEntity;
import persistence.manager.UserManager;
import persistence.manager.patcher.UserPatcher;
import persistence.model.ModelBuilder;
import persistence.model.User;
import api.rest.security.authentication.exception.AuthenticationException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
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

    @Inject
    @FriendRequest
    private Event<FriendRequestEvent> friendRequestEvent;

    @Inject
    @NewFriend
    private Event<FriendEvent> newFriendEvent;

    @Inject
    @DeleteFriend
    private Event<FriendEvent> deleteFriendEvent;

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

    public User getBasicUser(int id){
        try {
            return modelBuilder.huskUser(id);
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
                .map(modelBuilder::huskUser)
                .collect(Collectors.toList());
    }

    public List<User> searchUser(String str){
        return manager.search(str)
                .stream()
                .map(modelBuilder::huskUser)
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

    public void sendFriendRequest(int senderID, int receiverID){
        manager.addFriendRequest(senderID, receiverID);
        friendRequestEvent.fire(createFriendRequestEvent(senderID, receiverID));
    }

    public void confirmFriendRequest(int receiverID, int senderID){
        manager.confirmFriend(receiverID, senderID);
        newFriendEvent.fire(createFriendEvent(receiverID, senderID));
    }

    public void removeFriend(int id1, int id2){
        manager.removeFriend(id1, id2);
        deleteFriendEvent.fire(createFriendEvent(id1, id2));
    }

    private FriendRequestEvent createFriendRequestEvent(int senderID, int receiverID){
        UserEntity sender = manager.get(senderID);
        UserEntity receiver = manager.get(receiverID);
        BasicUserData senderData = new BasicUserData(sender.getId(), sender.getUsername());
        BasicUserData receiverData = new BasicUserData(receiver.getId(), receiver.getUsername());
        return new FriendRequestEvent(senderData, receiverData);
    }

    private FriendEvent createFriendEvent(int user1ID, int user2ID){
        UserEntity user1 = manager.get(user1ID);
        UserEntity user2 = manager.get(user2ID);
        BasicUserData senderData = new BasicUserData(user1.getId(), user1.getUsername());
        BasicUserData receiverData = new BasicUserData(user2.getId(), user2.getUsername());
        return new FriendEvent(senderData, receiverData);
    }
}
