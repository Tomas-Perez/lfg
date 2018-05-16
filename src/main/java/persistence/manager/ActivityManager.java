package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.generator.KeyGenerator;
import persistence.manager.patcher.ActivityPatcher;
import persistence.entity.ActivityEntity;

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
    private GameManager gameManager;

    @Inject
    public ActivityManager(EntityManager manager, KeyGenerator generator, GameManager gameManager) {
        this.manager = manager;
        this.generator = generator;
        this.gameManager = gameManager;
    }

    public ActivityManager(){ }

    public int addActivity(@NotNull String name, int game) throws ConstraintException {
        checkValidCreation(name, game);
        int id = generator.generate("activity");
        ActivityEntity activity = new ActivityEntity(id, name, game);
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
        ActivityEntity activity = manager.find(ActivityEntity.class, activityID);
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
            ActivityEntity activity = manager.find(ActivityEntity.class, activityID);
            manager.remove(activity);
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
    public Optional<ActivityEntity> getActivity(String name, int game){
        return manager
                .createQuery("FROM ActivityEntity A WHERE A.name = :name AND A.gameId = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList()
                .stream()
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> listActivities(){
        return manager.createQuery("SELECT A.id FROM ActivityEntity A").getResultList();
    }

    public boolean activityExists(@NotNull String name, int game){
        return manager
                .createQuery("SELECT 1 FROM ActivityEntity A WHERE A.name = :name AND A.gameId = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList().size() > 0;
    }

    public boolean activityExists(int activityID){
        return manager.find(ActivityEntity.class, activityID) != null;
    }

    private void checkValidCreation(@NotNull String name, int gameID) throws ConstraintException{
        if(!gameManager.gameExists(gameID))
            throw new ConstraintException(String.format("Game with id: %d does not exist", gameID));
        if(activityExists(name, gameID))
            throw new ConstraintException(String.format("%s for %d", name, gameID));
    }

    private void checkValidUpdate(@NotNull ActivityEntity activity, @NotNull ActivityPatcher patcher) throws ConstraintException {
        String nameToCheck = null;
        Integer gameToCheck = null;
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
            if(gameToCheck == null) gameToCheck = activity.getGameId();
            checkValidCreation(nameToCheck, gameToCheck);
        }
    }

    public void wipeAllRecords(){
        listActivities().forEach(this::deleteActivity);
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

    public ActivityEntity getActivity(int activityID){
        return manager.find(ActivityEntity.class, activityID);
    }

}
