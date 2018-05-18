package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
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
public class ActivityManager implements Manager<ActivityEntity>{
    private EntityManager manager;
    private GameManager gameManager;

    @Inject
    public ActivityManager(EntityManager manager, GameManager gameManager) {
        this.manager = manager;
        this.gameManager = gameManager;
    }

    public ActivityManager(){ }

    public int add(ActivityEntity activity) throws ConstraintException {
        checkValidCreation(activity.getName(), activity.getGameId());
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

    public void delete(int activityID){
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
    public Optional<ActivityEntity> get(String name, int game){
        return manager
                .createQuery("FROM ActivityEntity A WHERE A.name = :name AND A.gameId = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList()
                .stream()
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> list(){
        return manager.createQuery("SELECT A.id FROM ActivityEntity A").getResultList();
    }

    public boolean exists(@NotNull String name, int game){
        return manager
                .createQuery("SELECT 1 FROM ActivityEntity A WHERE A.name = :name AND A.gameId = :game")
                .setParameter("name", name)
                .setParameter("game", game)
                .getResultList().size() > 0;
    }

    private void checkValidCreation(@NotNull String name, int gameID) throws ConstraintException{
        if(!gameManager.exists(gameID))
            throw new ConstraintException(String.format("Game with id: %d does not exist", gameID));
        if(exists(name, gameID))
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

    public ActivityEntity get(int activityID){
        return manager.find(ActivityEntity.class, activityID);
    }

}
