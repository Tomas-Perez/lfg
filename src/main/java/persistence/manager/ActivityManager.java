package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.model.Activity;
import persistence.model.Game;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class ActivityManager {
    private EntityManager manager;

    @Inject
    public ActivityManager(EntityManager manager) {
        this.manager = manager;
    }

    public ActivityManager(){ }

    public void addActivity(@NotNull String name, @NotNull Game game){
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            Activity activity = new Activity(name, game);
            manager.persist(activity);
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

    public void updateActivity(int activityID, @NotNull String name, @NotNull Game game){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Activity activity = manager.find(Activity.class, activityID);
            activity.setName(name);
            activity.setGame(game);
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

    public void deleteActivity(int activityID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Activity activity = manager.find(Activity.class, activityID);
            manager.remove(activity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Activity> listActivity(){
        return manager.createQuery("FROM Activity").getResultList();
    }

    public Optional<Activity> getByName(@NotNull String name){
        List<Activity> users = manager
                .createQuery("FROM Activity A WHERE A.name = :name", Activity.class)
                .setParameter("name", name)
                .getResultList();
        return users.stream().findFirst();
    }
}
