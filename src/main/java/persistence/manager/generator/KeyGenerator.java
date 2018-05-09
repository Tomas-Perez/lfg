package persistence.manager.generator;

import org.jetbrains.annotations.NotNull;
import persistence.model.Key;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class KeyGenerator {

    private EntityManager manager;

    @Inject
    public KeyGenerator(EntityManager manager) {
        this.manager = manager;
    }

    public KeyGenerator(){ }

    private void addKey(@NotNull String targetTable){
        Key key = new Key(targetTable);
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.persist(key);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public int generate(@NotNull String targetTable){
        EntityTransaction tx = manager.getTransaction();
        int id = 0;
        boolean keyExists = false;
        try {
            tx.begin();
            Key key = manager.find(Key.class, targetTable);
            if(key != null){
                id = key.getAndIncrement();
                keyExists = true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        if(!keyExists){
            addKey(targetTable);
            id = generate(targetTable);
        }

        return id;
    }


}