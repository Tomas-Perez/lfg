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
import java.util.*;

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

    public void addFriendRequest(int sender, int receiver){
        checkFriendCreation(sender, receiver);
        FriendPair pair = FriendPair.request(sender, receiver);
        FriendEntity friendEntity = new FriendEntity(pair.first, pair.second, pair.status);
        persist(friendEntity);
    }

    public void confirmFriend(int receiver, int sender){
        EntityTransaction tx = manager.getTransaction();
        FriendPair pair = new FriendPair(receiver, sender);
        FriendEntityPK friendEntityPK = new FriendEntityPK(pair.first, pair.second);
        FriendEntity friendEntity = manager.find(FriendEntity.class, friendEntityPK);
        if(friendEntity == null) {
            final String constraintName =
                    String.format("Friend request between %d and %d does not exist", receiver, sender);
            throw new ConstraintException(constraintName);
        }
        if(!receivedRequest(receiver, friendEntity))
            throw new ConstraintException("Only the receiver of the request can confirm it");
        try {
            tx.begin();
            friendEntity.setStatus(FriendEntity.FriendStatus.CONFIRMED);
            tx.commit();
        }  catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    private boolean receivedRequest(int id, FriendEntity entity){
        boolean received = false;
        if(entity.getUser1Id() == id){
            if(entity.getStatus() == FriendEntity.FriendStatus.SECOND_SENT){
                received = true;
            }
        } else if(entity.getStatus() == FriendEntity.FriendStatus.FIRST_SENT) {
            received = true;
        }
        return received;
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

    private void checkUser(int userID){
        if(!exists(userID))
            throw new ConstraintException(String.format("User with id: %d does not exist", userID));
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getUserFriends(int userID){
        final List resultList = manager.createNativeQuery("SELECT CASE\n" +
                "WHEN F.USER_1_ID = :userID THEN F.USER_2_ID\n" +
                "ELSE F.USER_1_ID\n" +
                "END\n" +
                "FROM\n" +
                "(SELECT DISTINCT F.*\n" +
                "FROM FRIEND F\n" +
                "JOIN USER U on F.USER_1_ID = U.id OR F.USER_2_ID = U.id\n" +
                "WHERE U.id = :userID) F\n" +
                "WHERE F.STATUS = :confirmed")
                .setParameter("userID", userID)
                .setParameter("confirmed", FriendEntity.FriendStatus.CONFIRMED.ordinal())
                .getResultList();
        return new HashSet<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getSentRequests(int userID){
        final List resultList = manager.createNativeQuery("SELECT CASE\n" +
                "WHEN F.USER_1_ID = :userID THEN F.USER_2_ID\n" +
                "ELSE F.USER_1_ID\n" +
                "END\n" +
                "FROM\n" +
                "(SELECT DISTINCT F.*\n" +
                "FROM FRIEND F\n" +
                "JOIN USER U on F.USER_1_ID = U.id OR F.USER_2_ID = U.id\n" +
                "WHERE U.id = :userID) F\n" +
                "WHERE (F.USER_1_ID = :userID AND F.STATUS = :firstSent) OR\n" +
                "(F.USER_2_ID = :userID AND F.STATUS = :secondSent)")
                .setParameter("userID", userID)
                .setParameter("firstSent", FriendEntity.FriendStatus.FIRST_SENT.ordinal())
                .setParameter("secondSent", FriendEntity.FriendStatus.SECOND_SENT.ordinal())
                .getResultList();
        return new HashSet<>(resultList);
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getReceivedRequests(int userID){
        final List resultList = manager.createNativeQuery("SELECT CASE\n" +
                "WHEN F.USER_1_ID = :userID THEN F.USER_2_ID\n" +
                "ELSE F.USER_1_ID\n" +
                "END\n" +
                "FROM\n" +
                "(SELECT DISTINCT F.*\n" +
                "FROM FRIEND F\n" +
                "JOIN USER U on F.USER_1_ID = U.id OR F.USER_2_ID = U.id\n" +
                "WHERE U.id = :userID) F\n" +
                "WHERE (F.USER_1_ID = :userID AND F.STATUS = :secondSent) OR\n" +
                "(F.USER_2_ID = :userID AND F.STATUS = :firstSent)")
                .setParameter("userID", userID)
                .setParameter("firstSent", FriendEntity.FriendStatus.FIRST_SENT.ordinal())
                .setParameter("secondSent", FriendEntity.FriendStatus.SECOND_SENT.ordinal())
                .getResultList();
        return new HashSet<>(resultList);
    }

    public boolean friendshipExists(int user1ID, int user2ID){
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntityPK pk = new FriendEntityPK(pair.first, pair.second);
        return manager.find(FriendEntity.class, pk) != null;
    }

    private static class FriendPair{
        int first;
        int second;
        FriendEntity.FriendStatus status;

        private FriendPair(int user1, int user2) {
            if(user1 < user2){
                this.first = user1;
                this.second = user2;
            } else {
                this.first = user2;
                this.second = user1;
            }
        }

        private static FriendPair request(int sender, int receiver){
            FriendPair friendPair = new FriendPair(sender, receiver);
            friendPair.status = sender < receiver?
                    FriendEntity.FriendStatus.FIRST_SENT : FriendEntity.FriendStatus.SECOND_SENT;
            return friendPair;
        }
    }
}
