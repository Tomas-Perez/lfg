package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.entity.GamePlatformEntity;
import persistence.entity.GamePlatformForPostEntity;
import persistence.manager.exception.ConstraintException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GamePlatformManager extends Manager<GamePlatformEntity> {

    @Inject
    public GamePlatformManager(EntityManager manager) {
        super(manager);
    }

    public GamePlatformManager() {
    }

    @Override
    public int add(GamePlatformEntity entity) {
        checkValidCreation(entity.getName());
        persist(entity);
        return entity.getId();
    }

    @Override
    public GamePlatformEntity get(int id) {
        return manager.find(GamePlatformEntity.class, id);
    }

    @Override
    public void delete(int id) {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            GamePlatformEntity gamePlatform = manager.find(GamePlatformEntity.class, id);
            manager.remove(gamePlatform);
            tx.commit();
        } catch (NullPointerException | IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> list() {
        return manager.createQuery("SELECT G.id FROM GamePlatformEntity G").getResultList();
    }

    @Override
    public void checkExistence(int id) {
        if(!exists(id))
            throw new ConstraintException(String.format("Game platform with id: %d does not exist", id));
    }

    private void checkValidCreation(@NotNull String name) throws ConstraintException{
        if(exists(name)) throw new ConstraintException(name);
    }

    public boolean exists(@NotNull String name){
        return manager
                .createQuery("SELECT 1 FROM GamePlatformEntity G WHERE G.name = :name")
                .setParameter("name", name)
                .getResultList().size() > 0;
    }


}
