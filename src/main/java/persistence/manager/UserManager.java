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
public class UserManager {

    private EntityManager manager;

    @Inject
    public UserManager(EntityManager manager) {
        this.manager = manager;
    }

    public UserManager(){ }

    public int addUser(@NotNull String username,
                        @NotNull String password,
                        @NotNull String email,
                        boolean isAdmin) throws ConstraintException
    {
        checkValidCreation(username, email);
        EntityTransaction tx = manager.getTransaction();
        UserEntity user = new UserEntity(isAdmin, email, password, username);

        try {
            tx.begin();
            manager.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return user.getId();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> listUsers(){
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


    public void deleteUser(int userID){
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

    public void addFriend(int user1ID, int user2ID){
        checkUser(user1ID);
        checkUser(user2ID);
        EntityTransaction tx = manager.getTransaction();
        FriendEntity friendEntity = new FriendEntity(user1ID, user2ID);
        try {
            tx.begin();
            manager.persist(friendEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void removeFriend(int user1ID, int user2ID){
        checkUser(user1ID);
        checkUser(user2ID);
        EntityTransaction tx = manager.getTransaction();
        FriendEntityPK friendEntityPK1 = new FriendEntityPK(user1ID, user2ID);
        FriendEntityPK friendEntityPK2 = new FriendEntityPK(user1ID, user2ID);
        try {
            tx.begin();
            FriendEntity friendEntity1 = manager.find(FriendEntity.class, friendEntityPK1);
            FriendEntity friendEntity2 = manager.find(FriendEntity.class, friendEntityPK2);
            if(friendEntity1 != null) manager.remove(friendEntity1);
            if(friendEntity2 != null) manager.remove(friendEntity2);
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

    public void wipeAllRecords(){
        listUsers().forEach(this::deleteUser);
//        EntityTransaction tx = manager.getTransaction();
//        try {
//            tx.begin();
//            manager.createQuery("DELETE FROM UserEntity").executeUpdate();
//            tx.commit();
//        } catch (Exception e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }
    }

    public UserEntity getUser(int userID){
        return manager.find(UserEntity.class, userID);
    }

    public boolean userExists(int userID){
        return getUser(userID) != null;
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
        if(!userExists(userID))
            throw new ConstraintException(String.format("User with id: %d does not exist", userID));
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUserFriends(int userID){
        return manager.createQuery(
                "SELECT " +
                "CASE WHEN F.user2Id != U.id THEN F.user2Id ELSE F.user1Id END " +
                "FROM FriendEntity F " +
                "JOIN UserEntity U on F.user1Id = U.id OR F.user2Id = U.id " +
                "WHERE U.id = :userID")
                .setParameter("userID", userID)
                .getResultList();
    }

}
