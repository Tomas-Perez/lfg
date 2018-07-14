package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.entity.ChatPlatformEntity;
import persistence.entity.ChatPlatformForPostEntity;
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
public class ChatPlatformManager extends Manager<ChatPlatformEntity> {


    @Inject
    public ChatPlatformManager(EntityManager manager) {
        super(manager);
    }

    public ChatPlatformManager() {
    }

    @Override
    public int add(ChatPlatformEntity entity) {
        checkValidCreation(entity.getName());
        persist(entity);
        return entity.getId();
    }

    @Override
    public ChatPlatformEntity get(int id) {
        return manager.find(ChatPlatformEntity.class, id);
    }

    @Override
    public void delete(int id) {
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            ChatPlatformEntity chatPlatform = manager.find(ChatPlatformEntity.class, id);
            manager.remove(chatPlatform);
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
        return manager.createQuery("SELECT C.id FROM ChatPlatformEntity C").getResultList();
    }

    @Override
    public void checkExistence(int id) {
        if(!exists(id))
            throw new ConstraintException(String.format("Chat platform with id: %d does not exist", id));
    }

    private void checkValidCreation(@NotNull String name) throws ConstraintException{
        if(exists(name)) throw new ConstraintException(name);
    }

    public boolean exists(@NotNull String name){
        return manager
                .createQuery("SELECT 1 FROM ChatPlatformEntity C WHERE C.name = :name")
                .setParameter("name", name)
                .getResultList().size() > 0;
    }
}
