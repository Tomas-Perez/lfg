package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.GamePatcher;
import persistence.entity.GameEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class GameManager {
    private EntityManager manager;

    @Inject
    public GameManager(EntityManager manager) {
        this.manager = manager;
    }

    public GameManager(){ }

    public int addGame(@NotNull String name, String image) throws ConstraintException {
        checkValidCreation(name);
        GameEntity game = new GameEntity(image, name);
        System.out.println(game.getId());
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.persist(game);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        return game.getId();
    }

    public void updateGame(int gameID, GamePatcher patcher)  throws ConstraintException
    {
        checkValidUpdate(patcher);

        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            GameEntity game = manager.find(GameEntity.class, gameID);
            patcher.patch(game);
            tx.commit();
        } catch (NullPointerException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void deleteGame(int gameID) throws NoSuchElementException{
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            GameEntity game = manager.find(GameEntity.class, gameID);
            manager.remove(game);
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
    public List<Integer> listGames(){
        return manager.createQuery("SELECT G.id FROM GameEntity G").getResultList();
    }

    public Optional<GameEntity> getByName(@NotNull String name){
        List<GameEntity> users = manager
                .createQuery("FROM GameEntity G WHERE G.name = :name", GameEntity.class)
                .setParameter("name", name)
                .getResultList();
        return users.stream().findFirst();
    }

    private void checkValidCreation(@NotNull String name) throws ConstraintException{
        if(gameExists(name)) throw new ConstraintException(name);
    }

    private void checkValidUpdate(@NotNull GamePatcher patcher){
        if(patcher.patchesName())
            checkValidCreation(patcher.getName());
    }

    public boolean gameExists(@NotNull String name){
        return manager
                .createQuery("SELECT 1 FROM GameEntity G WHERE G.name = :name")
                .setParameter("name", name)
                .getResultList().size() > 0;
    }

    public void wipeAllRecords(){
        listGames().forEach(this::deleteGame);
//        EntityTransaction tx = manager.getTransaction();
//        try {
//            tx.begin();
//            manager.createQuery("DELETE FROM GameEntity").executeUpdate();
//            tx.commit();
//        } catch (Exception e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }
    }

    public GameEntity getGame(int gameID){
        return manager.find(GameEntity.class, gameID);
    }

    public boolean gameExists(int gameID){
        return manager.find(GameEntity.class, gameID) != null;
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getGameActivities(int gameID){
        return new HashSet<>((manager.createQuery("SELECT A.id FROM ActivityEntity A WHERE A.gameId = :game")
                .setParameter("game", gameID)
                .getResultList()));
    }
}
