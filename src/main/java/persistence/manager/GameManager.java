package persistence.manager;

import org.jetbrains.annotations.NotNull;
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
public class GameManager {
    private EntityManager manager;

    @Inject
    public GameManager(EntityManager manager) {
        this.manager = manager;
    }

    public GameManager(){ }

    public void addGame(@NotNull String name, String image){
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            Game game = new Game(name, image);
            manager.persist(game);
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

    public void updateGame(int gameID,
                           @NotNull String name,
                           String image)
    {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Game game = manager.find(Game.class, gameID);
            game.setName(name);
            game.setImage(image);
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
}
