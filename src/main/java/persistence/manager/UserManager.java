package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.UserPatcher;
import persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;

@ApplicationScoped
public class UserManager extends Manager<UserEntity>{

    private FriendHelperManager friendHelper;

    @Inject
    public UserManager(EntityManager manager, FriendHelperManager friendHelper) {
        super(manager);
        this.friendHelper = friendHelper;
    }

    public UserManager(){}

    public int add(UserEntity user) throws ConstraintException {
        checkValidCreation(user.getUsername(), user.getEmail());
        persist(user);
        return user.getId();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> list(){
        return manager.createQuery("SELECT U.id FROM UserEntity U").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> search(String str){
        return manager.createQuery(
                "SELECT U.id " +
                "FROM UserEntity U " +
                "WHERE U.email LIKE :str OR U.username LIKE :str")
                .setParameter("str", str.toLowerCase() + "%")
                .getResultList();
    }

    public void updateUser(int userID, @NotNull UserPatcher patcher) throws ConstraintException{
        checkValidUpdate(patcher);
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            UserEntity user = manager.find(UserEntity.class, userID);
            patcher.patch(user);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void delete(int userID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            UserEntity user = manager.find(UserEntity.class, userID);
            manager.remove(user);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Optional<UserEntity> getByEmail(@NotNull String email){
        List<UserEntity> users = manager
                .createQuery("FROM UserEntity U WHERE U.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }

    private void checkValidCreation(@NotNull String username, @NotNull String email) throws ConstraintException{
        if(userExistsByUserName(username)) throw new ConstraintException(username);
        if(userExistsByEmail(email)) throw new ConstraintException(email);
    }

    private void checkValidUpdate(@NotNull UserPatcher patcher) throws ConstraintException{
        if(patcher.patchesUsername() && userExistsByUserName(patcher.getUsername()))
            throw new ConstraintException(patcher.getUsername());

        if(patcher.patchesEmail() && userExistsByEmail(patcher.getEmail()))
            throw new ConstraintException(patcher.getEmail());
    }

    public boolean userExistsByUserName(@NotNull String username){
        return manager
                .createQuery("SELECT 1 FROM UserEntity U WHERE U.username = :username")
                .setParameter("username", username)
                .getResultList().size() > 0;
    }

    public boolean userExistsByEmail(@NotNull String email){
        return manager
                .createQuery("SELECT 1 FROM UserEntity U WHERE U.email = :email")
                .setParameter("email", email)
                .getResultList().size() > 0;
    }

    public UserEntity get(int userID){
        return manager.find(UserEntity.class, userID);
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getUserGroups(int userID){
        final List resultList = manager.createQuery(
                "SELECT DISTINCT M.groupId " +
                        "FROM GroupMemberEntity M JOIN GroupEntity G ON G.id = M.groupId " +
                        "WHERE M.memberId = :userID")
                .setParameter("userID", userID)
                .getResultList();
        return new HashSet<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getUserGames(int userID){
        final List resultList = manager.createQuery(
                "SELECT DISTINCT O.gameId " +
                        "FROM OwnsGameEntity O JOIN GameEntity G ON G.id = O.gameId " +
                        "WHERE O.ownerId = :userID")
                .setParameter("userID", userID)
                .getResultList();
        return new HashSet<>(resultList);
    }

    public void checkExistence(int userID){
        if(!exists(userID))
            throw new ConstraintException(String.format("User with id: %d does not exist", userID));
    }

    public void addFriendRequest(int sender, int receiver){
        checkExistence(sender);
        checkExistence(receiver);
        friendHelper.addFriendRequest(manager, sender, receiver);
    }

    public void confirmFriend(int receiver, int sender){
        friendHelper.confirmFriend(manager, receiver, sender);
    }

    public void removeFriend(int user1ID, int user2ID){
        friendHelper.removeFriend(manager, user1ID, user2ID);
    }

    public Set<Integer> getUserFriends(int userID){
        return friendHelper.getUserFriends(manager, userID);
    }

    public Set<Integer> getSentRequests(int userID){
        return friendHelper.getSentRequests(manager, userID);
    }

    public Set<Integer> getReceivedRequests(int userID){
        return friendHelper.getReceivedRequests(manager, userID);
    }
}
