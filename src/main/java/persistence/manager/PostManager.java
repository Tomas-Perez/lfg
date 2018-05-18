package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class PostManager implements Manager<PostEntity>{
    private EntityManager manager;
    private UserManager userManager;
    private ActivityManager activityManager;
    private GroupManager groupManager;

    @Inject
    public PostManager(EntityManager manager, UserManager userManager, ActivityManager activityManager, GroupManager groupManager) {
        this.manager = manager;
        this.userManager = userManager;
        this.activityManager = activityManager;
        this.groupManager = groupManager;
    }

    public PostManager(){ }

    public int add(PostEntity post)
    {
        checkValidCreation(post.getOwnerId(), post.getActivityId());
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            manager.persist(post);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return post.getId();
    }

    public int addGroupPost(String description,
                             @NotNull LocalDateTime date,
                             @NotNull GroupEntity group)
    {
        EntityTransaction tx = manager.getTransaction();
        Integer groupOwner = groupManager.getGroupOwner(group.getId());
        PostEntity post = new PostEntity(
                description,
                date,
                group.getActivityId(),
                groupOwner,
                group.getId()
        );

        try {
            tx.begin();
            manager.persist(post);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return post.getId();
    }

    public void delete(int postID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            PostEntity post = manager.find(PostEntity.class, postID);
            manager.remove(post);
            tx.commit();
        } catch (NullPointerException | IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> list(){
        return manager.createQuery("SELECT P.id FROM PostEntity P").getResultList();
    }

    public PostEntity get(int postID){
        return manager.find(PostEntity.class, postID);
    }

    private void checkValidCreation(int ownerID, int activityID){
        checkUser(ownerID);
        checkActivity(activityID);
    }

    private void checkUser(int ownerID){
        if(!userManager.exists(ownerID))
            throw new ConstraintException(String.format("User with id: %d does not exist", ownerID));
    }

    private void checkActivity(int activityID){
        if(!activityManager.exists(activityID))
            throw new ConstraintException(String.format("Activity with id: %d does not exist", activityID));
    }
}
