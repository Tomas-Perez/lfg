package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupManager {
    private EntityManager manager;

    @Inject
    public GroupManager(EntityManager manager) {
        this.manager = manager;
    }

    public GroupManager(){ }

    public int addGroup(int slots,
                         @NotNull Activity activity,
                         @NotNull User owner,
                         ChatPlatform chatPlatform,
                         GamePlatform gamePlatform)
    {
        EntityTransaction tx = manager.getTransaction();
        Group group = new Group(slots, activity, owner, chatPlatform, gamePlatform);

        try {
            tx.begin();
            manager.persist(group);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return group.getId();
    }

    public Group getGroup(int groupID){
        return manager.find(Group.class, groupID);
    }

    public void addMemberToGroup(int groupID, User member){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Group group = manager.find(Group.class, groupID);
            group.addMember(member);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void removeMemberFromGroup(int groupID, User member){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Group group = manager.find(Group.class, groupID);
            group.removeMember(member);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void deleteGroup(int groupID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Group group = manager.find(Group.class, groupID);
            manager.remove(group);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Group> listGroups(){
        return manager.createQuery("FROM Group").getResultList();
    }

    public void wipeAllRecords(){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.createQuery("DELETE FROM Group").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }
}