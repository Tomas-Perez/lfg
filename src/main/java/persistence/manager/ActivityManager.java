package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.generator.KeyGenerator;
import persistence.manager.patcher.ActivityPatcher;
import persistence.model.Activity;
import persistence.model.Game;
import persistence.model.Group;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class ActivityManager {
    private EntityManager manager;
    private KeyGenerator generator;

    @Inject
    public ActivityManager(EntityManager manager, KeyGenerator generator) {
        this.manager = manager;
        this.generator = generator;
    }

    public ActivityManager(){ }

    public int addActivity(@NotNull String name, @NotNull Game game) throws ConstraintException {
        checkValidCreation(name, game);
        int id = generator.generate("activity");
        Activity activity = new Activity(id, name, game);
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            manager.persist(activity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return activity.getId();
    }

    public void updateActivity(int activityID, @NotNull ActivityPatcher patcher) throws ConstraintException{
        Activity activity = manager.find(Activity.class, activityID);
        checkValidUpdate(activity, patcher);
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            patcher.patch(activity);
            tx.commit();
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
            activity.destroy();
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
    public Optional<Activity> getActivity(String name, Game game){
        return manager
                .createQuery("FROM Activity A WHERE A.name = :name AND A.game = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList()
                .stream()
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public List<Activity> listActivities(){
        return manager.createQuery("FROM Activity").getResultList();
    }

    public boolean activityExists(@NotNull String name, @NotNull Game game){
        return manager
                .createQuery("SELECT 1 FROM Activity A WHERE A.name = :name AND A.game = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList().size() > 0;
    }

    private void checkValidCreation(@NotNull String name, @NotNull Game game) throws ConstraintException{
        if(activityExists(name, game)) throw new ConstraintException(String.format("%s for %s", name, game.getName()));
    }

    private void checkValidUpdate(@NotNull Activity activity, @NotNull ActivityPatcher patcher) throws ConstraintException {
        String nameToCheck = null;
        Game gameToCheck = null;
        boolean check = false;
        if (patcher.patchesGame()) {
            gameToCheck = patcher.getGame();
            check = true;
        }
        if (patcher.patchesName()){
            nameToCheck = patcher.getName();
            check = true;
        }

        if(check){
            if(nameToCheck == null) nameToCheck = activity.getName();
            if(gameToCheck == null) gameToCheck = activity.getGame();
            checkValidCreation(nameToCheck, gameToCheck);
        }
    }

    public void wipeAllRecords(){
        listActivities().stream().map(Activity::getId).forEach(this::deleteActivity);
//        EntityTransaction tx = manager.getTransaction();
//        try {
//            tx.begin();
//            manager.createQuery("DELETE FROM Activity").executeUpdate();
//            tx.commit();
//        } catch (Exception e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }
    }

    public Activity getActivity(int activityID){
        return manager.find(Activity.class, activityID);
    }
}
