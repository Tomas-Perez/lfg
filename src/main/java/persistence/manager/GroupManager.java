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
public class GroupManager extends Manager<GroupEntity>{
    private UserManager userManager;
    private ActivityManager activityManager;

    @Inject
    public GroupManager(
            EntityManager manager,
            UserManager userManager,
            ActivityManager activityManager)
    {
        super(manager);
        this.userManager = userManager;
        this.activityManager = activityManager;
    }

    public GroupManager(){}


    public int add(GroupEntity group) {
        checkValidCreation(group.getOwnerId(), group.getActivityId());
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
        boolean deleteGroup = false;
        try {
            tx.begin();
            GroupMemberEntity groupMemberEntity = manager.find(GroupMemberEntity.class, key);
            if(groupMemberEntity == null) throw new IllegalArgumentException();
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
            delete(groupID);
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
        return (Integer) manager.createQuery("SELECT M.memberId " +
                "FROM GroupMemberEntity M " +
                "WHERE M.groupId = :groupID AND M.owner = true")
                .setParameter("groupID", groupID)
                .getSingleResult();
    }

    private void checkValidCreation(int ownerID, int activityID){
        userManager.checkExistence(ownerID);
        activityManager.checkExistence(activityID);
    }

    public void checkExistence(int groupID){
        if(!exists(groupID))
            throw new ConstraintException(String.format("Group with id: %d does not exist", groupID));
    }
}