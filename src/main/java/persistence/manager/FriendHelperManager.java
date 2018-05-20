package persistence.manager;

import persistence.entity.FriendEntity;
import persistence.entity.FriendEntityPK;
import persistence.manager.exception.ConstraintException;
import persistence.manager.util.ManagerUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class FriendHelperManager {

    void addFriendRequest(EntityManager manager, int sender, int receiver){
        checkFriendCreation(manager, sender, receiver);
        FriendPair pair = FriendPair.request(sender, receiver);
        FriendEntity friendEntity = new FriendEntity(pair.first, pair.second, pair.status);
        ManagerUtil.persist(manager, friendEntity);
    }

    void confirmFriend(EntityManager manager, int receiver, int sender){
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

    void removeFriend(EntityManager manager, int user1ID, int user2ID){
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


    @SuppressWarnings("unchecked")
    Set<Integer> getUserFriends(EntityManager manager, int userID){
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
    Set<Integer> getSentRequests(EntityManager manager, int userID){
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
    Set<Integer> getReceivedRequests(EntityManager manager, int userID){
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

    private boolean friendshipExists(EntityManager manager, int user1ID, int user2ID){
        FriendPair pair = new FriendPair(user1ID, user2ID);
        FriendEntityPK pk = new FriendEntityPK(pair.first, pair.second);
        return manager.find(FriendEntity.class, pk) != null;
    }

    private void checkFriendCreation(EntityManager manager, int user1ID, int user2ID){
        if(friendshipExists(manager, user1ID, user1ID)) {
            final String constraintName =
                    String.format("Friendship between %d and %d already exists", user1ID, user2ID);
            throw new ConstraintException(constraintName);
        }
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
