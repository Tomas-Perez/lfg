package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.model.Game;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

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

    public void addGame(@NotNull String name, String image) throws ConstraintException {
        checkValidCreation(name);

        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Game game = new Game(name, image);
            manager.persist(game);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void updateGame(int gameID,
                           @NotNull String name,
                           String image)  throws ConstraintException
    {
        checkValidCreation(name);

        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Game game = manager.find(Game.class, gameID);
            game.setName(name);
            game.setImage(image);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void deleteGame(int gameID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Game game = manager.find(Game.class, gameID);
            manager.remove(game);
            tx.commit();
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

    public boolean gameExists(@NotNull String name){
        return manager
                .createQuery("SELECT 1 FROM Game G WHERE G.name = :name")
                .setParameter("name", name)
                .getResultList().size() > 0;
    }

    public void wipeAllRecords(){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.createQuery("DELETE FROM Game").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }
}
