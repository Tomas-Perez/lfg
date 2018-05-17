package persistence.manager;

import persistence.manager.exception.ConstraintException;
import persistence.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupManager {
    private EntityManager manager;
    private UserManager userManager;
    private ActivityManager activityManager;

    @Inject
    public GroupManager(
            EntityManager manager,
            UserManager userManager,
            ActivityManager activityManager)
    {
        this.manager = manager;
        this.userManager = userManager;
        this.activityManager = activityManager;
    }

    public GroupManager(){}

    public int addGroup(int slots,
                        int activityID,
                        int ownerID,
                        Integer chatPlatformID,
                        Integer gamePlatformID)
    {
        checkValidCreation(ownerID, activityID);
        EntityTransaction tx = manager.getTransaction();
        GroupEntity group = new GroupEntity(slots, activityID, chatPlatformID, gamePlatformID);

        try {
            tx.begin();
            manager.persist(group);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        tx = manager.getTransaction();
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(group.getId(), ownerID, true);
        try {
            tx.begin();
            manager.persist(groupMemberEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }


        return group.getId();
    }

    public GroupEntity getGroup(int groupID){
        return manager.find(GroupEntity.class, groupID);
    }

    public void addMemberToGroup(int groupID, int memberID){
        checkUser(memberID);
        EntityTransaction tx = manager.getTransaction();
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupID, memberID, false);
        try {
            tx.begin();
            manager.persist(groupMemberEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void removeMemberFromGroup(int groupID, int memberID){
        EntityTransaction tx = manager.getTransaction();
        GroupMemberEntityPK key = new GroupMemberEntityPK(groupID, memberID);
        boolean deleteGroup = false;
        try {
            tx.begin();
            GroupMemberEntity groupMemberEntity = manager.find(GroupMemberEntity.class, key);
            if(groupMemberEntity.isOwner())
                deleteGroup = true;
            manager.remove(groupMemberEntity);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        if(deleteGroup)
            deleteGroup(groupID);
    }

    public void deleteGroup(int groupID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            GroupEntity group = manager.find(GroupEntity.class, groupID);
            manager.remove(group);
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
    public List<Integer> listGroups(){
        return manager.createQuery("SELECT G.id FROM GroupEntity G").getResultList();
    }

    public void wipeAllRecords(){
        listGroups().forEach(this::deleteGroup);
//        EntityTransaction tx = manager.getTransaction();
//        try {
//            tx.begin();
//            manager.createQuery("DELETE FROM GroupEntity").executeUpdate();
//            tx.commit();
//        } catch (Exception e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGroupMembers(int groupID){
        return manager.createQuery("SELECT M.memberId " +
                "FROM GroupMemberEntity M JOIN UserEntity U ON U.id = M.memberId " +
                "WHERE M.groupId = :groupID")
                .setParameter("groupID", groupID)
                .getResultList();
    }

    public Integer getGroupOwner(int groupID){
        return (Integer) manager.createQuery("SELECT M.memberId " +
                "FROM GroupMemberEntity M JOIN UserEntity U ON U.id = M.memberId " +
                "WHERE M.groupId = :groupID AND M.owner = true")
                .setParameter("groupID", groupID)
                .getSingleResult();
    }

    public boolean groupExists(int groupID){
        return manager.find(GroupEntity.class, groupID) != null;
    }

    private void checkValidCreation(int ownerID, int activityID){
        checkUser(ownerID);
        checkActivity(activityID);
    }

    private void checkUser(int ownerID){
        if(!userManager.userExists(ownerID))
            throw new ConstraintException(String.format("User with id: %d does not exist", ownerID));
    }

    private void checkActivity(int activityID){
        if(!activityManager.activityExists(activityID))
            throw new ConstraintException(String.format("Activity with id: %d does not exist", activityID));
    }
}