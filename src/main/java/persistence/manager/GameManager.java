package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.generator.KeyGenerator;
import persistence.manager.patcher.GamePatcher;
import persistence.model.Activity;
import persistence.model.Game;

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
public class GameManager {
    private EntityManager manager;
    private KeyGenerator generator;

    @Inject
    public GameManager(EntityManager manager, KeyGenerator generator) {
        this.manager = manager;
        this.generator = generator;
    }

    public GameManager(){ }

    public int addGame(@NotNull String name, String image) throws ConstraintException {
        checkValidCreation(name);
        int id = generator.generate("game");
        Game game = new Game(id, name, image);
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
            Game game = manager.find(Game.class, gameID);
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
            Game game = manager.find(Game.class, gameID);
            manager.remove(game);
            game.destroy();
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
    public List<Game> listGames(){
        return manager.createQuery("FROM Game").getResultList();
    }

    public Optional<Game> getByName(@NotNull String name){
        List<Game> users = manager
                .createQuery("FROM Game G WHERE G.name = :name", Game.class)
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
                .createQuery("SELECT 1 FROM Game G WHERE G.name = :name")
                .setParameter("name", name)
                .getResultList().size() > 0;
    }

    public void wipeAllRecords(){
        listGames().stream().map(Game::getId).forEach(this::deleteGame);
//        EntityTransaction tx = manager.getTransaction();
//        try {
//            tx.begin();
//            manager.createQuery("DELETE FROM Game").executeUpdate();
//            tx.commit();
//        } catch (Exception e) {
//            if (tx!=null) tx.rollback();
//            e.printStackTrace();
//        }
    }

    public Game getGame(int gameID){
        return manager.find(Game.class, gameID);
    }
}
