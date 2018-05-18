package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.entity.FriendEntity;
import persistence.entity.FriendEntityPK;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.UserPatcher;
import persistence.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationScoped
public class UserManager extends Manager<UserEntity>{

    @Inject
    public UserManager(EntityManager manager) {
        super(manager);
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

    public void addFriendRequest(int user1ID, int user2ID){
        checkFriendCreation(user1ID, user2ID);
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntity friendEntity = new FriendEntity(pair.first, pair.second, false);
        persist(friendEntity);
    }

    public void confirmFriend(int user1ID, int user2ID){
        EntityTransaction tx = manager.getTransaction();
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntityPK friendEntityPK = new FriendEntityPK(pair.first, pair.second);
        try {
            tx.begin();
            FriendEntity friendEntity = manager.find(FriendEntity.class, friendEntityPK);
            friendEntity.setConfirmed(true);
            tx.commit();
        } catch (NullPointerException exc) {
            if (tx != null) tx.rollback();
            final String constraintName =
                    String.format("Friend request between %d and %d does not exist", user1ID, user2ID);
            throw new ConstraintException(constraintName);
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void removeFriend(int user1ID, int user2ID){
        EntityTransaction tx = manager.getTransaction();
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntityPK friendEntityPK = new FriendEntityPK(pair.first, pair.second);
        try {
            tx.begin();
            FriendEntity friendEntity = manager.find(FriendEntity.class, friendEntityPK);
            manager.remove(friendEntity);
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

    private void checkFriendCreation(int user1ID, int user2ID){
        if(friendshipExists(user1ID, user1ID)) {
            final String constraintName =
                    String.format("Friendship between %d and %d already exists", user1ID, user2ID);
            throw new ConstraintException(constraintName);
        }
        checkUser(user1ID);
        checkUser(user2ID);
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
    public List<Integer> getUserGroups(int userID){
        return manager.createQuery("SELECT M.groupId " +
                "FROM GroupMemberEntity M JOIN GroupEntity G ON G.id = M.groupId " +
                "WHERE M.memberId = :userID")
                .setParameter("userID", userID)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUserGames(int userID){
        return manager.createQuery("SELECT O.gameId " +
                "FROM OwnsGameEntity O JOIN GameEntity G ON G.id = O.gameId " +
                "WHERE O.ownerId = :userID")
                .setParameter("userID", userID)
                .getResultList();
    }

    private void checkUser(int userID){
        if(!exists(userID))
            throw new ConstraintException(String.format("User with id: %d does not exist", userID));
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUserFriends(int userID){
        return manager.createQuery(
                "SELECT " +
                        "CASE WHEN F.user2Id != U.id THEN F.user2Id ELSE F.user1Id END " +
                        "FROM FriendEntity F " +
                        "JOIN UserEntity U on F.user1Id = U.id OR F.user2Id = U.id " +
                        "WHERE U.id = :userID AND F.confirmed")
                .setParameter("userID", userID)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUserFriendRequests(int userID){
        return manager.createQuery(
                "SELECT " +
                        "CASE WHEN F.user2Id != U.id THEN F.user2Id ELSE F.user1Id END " +
                        "FROM FriendEntity F " +
                        "JOIN UserEntity U on F.user1Id = U.id OR F.user2Id = U.id " +
                        "WHERE U.id = :userID AND NOT F.confirmed")
                .setParameter("userID", userID)
                .getResultList();
    }

    public boolean friendshipExists(int user1ID, int user2ID){
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntityPK pk = new FriendEntityPK(pair.first, pair.second);
        return manager.find(FriendEntity.class, pk) != null;
    }

    private class FriendPair{
        int first;
        int second;

        private FriendPair(int user1, int user2) {
            if(user1 < user2){
                this.first = user1;
                this.second = user2;
            } else {
                this.first = user2;
                this.second = user1;
            }
        }
    }
}
