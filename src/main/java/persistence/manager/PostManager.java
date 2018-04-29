package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.model.Activity;
import persistence.model.Group;
import persistence.model.Post;
import persistence.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class PostManager {
    private EntityManager manager;

    @Inject
    public PostManager(EntityManager manager) {
        this.manager = manager;
    }

    public PostManager(){ }

    public void addPost(String description,
                        @NotNull LocalDateTime date,
                        Activity activity,
                        @NotNull User owner)
    {
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            Post post = new Post(description, date, activity, owner);
            manager.persist(post);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void addGroupPost(String description,
                             @NotNull LocalDateTime date,
                             @NotNull Group group)
    {
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            Post post = new Post(description, date, group);
            manager.persist(post);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void deletePost(int postID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Post post = manager.find(Post.class, postID);
            manager.remove(post);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Post> listPosts(){
        return manager.createQuery("FROM Post").getResultList();
    }

    public void wipeAllRecords(){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.createQuery("DELETE FROM Post").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }
}
