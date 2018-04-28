package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
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

    public void addGroup(int slots,
                         @NotNull Activity activity,
                         @NotNull User owner,
                         ChatPlatform chatPlatform,
                         GamePlatform gamePlatform)
    {
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            Group group = new Group(slots, activity, owner, chatPlatform, gamePlatform);
            manager.persist(group);
            tx.commit();
        } catch (PersistenceException e){
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            throw new ConstraintException(e);
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void addMemberToGroup(int groupID, User member){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Group group = manager.find(Group.class, groupID);
            group.addMember(member);
            tx.commit();
        } catch (PersistenceException e){
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            throw new ConstraintException(e);
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
}