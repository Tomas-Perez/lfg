package persistence.manager;

import persistence.manager.exception.ConstraintException;
import persistence.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupManager extends Manager<GroupEntity>{
    private UserManager userManager;
    private ActivityManager activityManager;
    private ChatPlatformManager chatPlatformManager;
    private GamePlatformManager gamePlatformManager;

    @Inject
    public GroupManager(
            EntityManager manager,
            UserManager userManager,
            ActivityManager activityManager,
            ChatPlatformManager chatPlatformManager,
            GamePlatformManager gamePlatformManager)
    {
        super(manager);
        this.userManager = userManager;
        this.activityManager = activityManager;
        this.chatPlatformManager = chatPlatformManager;
        this.gamePlatformManager = gamePlatformManager;
    }

    public GroupManager(){}


    public int add(GroupEntity group) {
        checkValidCreation(group);
        persist(group);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(group.getId(), group.getOwnerId(), true);
        persist(groupMemberEntity);
        return group.getId();
    }

    public GroupEntity get(int groupID){
        final GroupEntity groupEntity = manager.find(GroupEntity.class, groupID);
        if(groupEntity != null) groupEntity.setOwnerId(getGroupOwner(groupID));
        return groupEntity;
    }

    public void addMemberToGroup(int groupID, int memberID){
        userManager.checkExistence(memberID);
        checkExistence(groupID);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupID, memberID, false);
        persist(groupMemberEntity);
    }

    public void removeMemberFromGroup(int groupID, int memberID){
        EntityTransaction tx = manager.getTransaction();
        GroupMemberEntityPK key = new GroupMemberEntityPK(groupID, memberID);
        boolean reassignOwner = false;
        try {
            tx.begin();
            GroupMemberEntity groupMemberEntity = manager.find(GroupMemberEntity.class, key);
            if(groupMemberEntity == null) throw new IllegalArgumentException();
            if(groupMemberEntity.isOwner()) reassignOwner = true;
            manager.remove(groupMemberEntity);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        if(emptyGroup(groupID)) delete(groupID);
        else if(reassignOwner) reassignRandomOwner(groupID);
    }

    public void delete(int groupID){
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
    public List<Integer> list(){
        return manager.createQuery("SELECT G.id FROM GroupEntity G").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGroupMembers(int groupID){
        return manager.createQuery("SELECT M.memberId " +
                "FROM GroupMemberEntity M " +
                "WHERE M.groupId = :groupID")
                .setParameter("groupID", groupID)
                .getResultList();
    }

    public Integer getGroupOwner(int groupID){
        try {
            return (Integer) manager.createQuery("SELECT M.memberId " +
                    "FROM GroupMemberEntity M " +
                    "WHERE M.groupId = :groupID AND M.owner = true")
                    .setParameter("groupID", groupID)
                    .getSingleResult();
        } catch (NoResultException exc){
            throw new NoSuchElementException(String.format("No owner for group with id: %d", groupID));
        }
    }

    public Integer getGroupChat(int groupID){
        try {
            return (Integer) manager.createQuery("SELECT C.id " +
                    "FROM ChatEntity C " +
                    "WHERE C.groupId = :groupID")
                    .setParameter("groupID", groupID)
                    .getSingleResult();
        } catch (NoResultException exc){
            throw new NoSuchElementException(String.format("No chat for group with id: %d", groupID));
        }
    }

    private void checkValidCreation(GroupEntity group){
        userManager.checkExistence(group.getOwnerId());
        activityManager.checkExistence(group.getActivityId());
        if(group.getChatPlatformId() != null) chatPlatformManager.checkExistence(group.getChatPlatformId());
        if(group.getGamePlatformId() != null) gamePlatformManager.checkExistence(group.getGamePlatformId());
    }

    public void checkExistence(int groupID){
        if(!exists(groupID))
            throw new ConstraintException(String.format("Group with id: %d does not exist", groupID));
    }

    private void reassignRandomOwner(int groupID){
        Integer newOwnerID = getNewOwnerID(groupID);
        assignOwner(groupID, newOwnerID);
    }

    private void assignOwner(int groupID, int newOwnerID){
        EntityTransaction tx = manager.getTransaction();
        GroupMemberEntityPK key = new GroupMemberEntityPK(groupID, newOwnerID);
        try {
            tx.begin();
            GroupMemberEntity groupMemberEntity = manager.find(GroupMemberEntity.class, key);
            groupMemberEntity.setOwner(true);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public int replaceOwner(int groupID, int newOwnerID){
        EntityTransaction tx = manager.getTransaction();
        GroupMemberEntityPK newOwnerKey = new GroupMemberEntityPK(groupID, newOwnerID);
        final Integer oldOwnerID = getGroupOwner(groupID);
        GroupMemberEntityPK oldOwnerKey = new GroupMemberEntityPK(groupID, oldOwnerID);
        try {
            tx.begin();
            GroupMemberEntity oldOwnerEntity = manager.find(GroupMemberEntity.class, oldOwnerKey);
            GroupMemberEntity newOwnerEntity = manager.find(GroupMemberEntity.class, newOwnerKey);
            if(oldOwnerEntity == null || newOwnerEntity == null) throw new IllegalArgumentException();
            oldOwnerEntity.setOwner(false);
            newOwnerEntity.setOwner(true);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return oldOwnerID;
    }

    private Integer getNewOwnerID(int groupID){
        return (Integer) manager.createQuery("SELECT M.memberId " +
                "FROM GroupMemberEntity M " +
                "WHERE M.groupId = :groupID AND M.owner = false")
                .setParameter("groupID", groupID)
                .getSingleResult();
    }

    private boolean emptyGroup(int groupID){
        return getGroupMembers(groupID).size() < 1;
    }
}