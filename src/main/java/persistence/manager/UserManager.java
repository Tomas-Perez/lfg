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
        checkUser(user1ID);
        checkUser(user2ID);
        FriendEntity friendEntity = new FriendEntity(user1ID, user2ID, false);
        persist(friendEntity);
    }

    public void confirmFriend(int user1ID, int user2ID){
        EntityTransaction tx = manager.getTransaction();
        FriendEntityPK friendEntityPK1 = new FriendEntityPK(user1ID, user2ID);
        FriendEntityPK friendEntityPK2 = new FriendEntityPK(user1ID, user2ID);
        try {
            boolean confirmed = false;
            tx.begin();
            FriendEntity friendEntity1 = manager.find(FriendEntity.class, friendEntityPK1);
            FriendEntity friendEntity2 = manager.find(FriendEntity.class, friendEntityPK2);
            if(friendEntity1 != null) {
                friendEntity1.setConfirmed(true);
                confirmed = true;
            }
            if(friendEntity2 != null && !confirmed) {
                friendEntity2.setConfirmed(true);
                confirmed = true;
            }
            if(!confirmed) {
                final String constraintName =
                        String.format("Friend request between %d and %d does not exist", user1ID, user2ID);
                throw new ConstraintException(constraintName);
            }
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (ConstraintException exc){
            if (tx!=null) tx.rollback();
            throw exc;
        }
        catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void removeFriend(int user1ID, int user2ID){
        EntityTransaction tx = manager.getTransaction();
        FriendEntityPK friendEntityPK1 = new FriendEntityPK(user1ID, user2ID);
        FriendEntityPK friendEntityPK2 = new FriendEntityPK(user1ID, user2ID);
        try {
            boolean removed = false;
            tx.begin();
            FriendEntity friendEntity1 = manager.find(FriendEntity.class, friendEntityPK1);
            FriendEntity friendEntity2 = manager.find(FriendEntity.class, friendEntityPK2);
            if(friendEntity1 != null){
                manager.remove(friendEntity1);
                removed = true;
            }
            if(friendEntity2 != null && !removed){
                manager.remove(friendEntity2);
                removed = true;
            }
            if(!removed) throw new IllegalArgumentException();
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

}
