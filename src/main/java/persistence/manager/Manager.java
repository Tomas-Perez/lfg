package persistence.manager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public abstract class Manager<T> {
    protected EntityManager manager;

    public Manager(EntityManager manager) {
        this.manager = manager;
    }

    public Manager(){ }

    public abstract int add(T entity);

    protected void persist(Object entity){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public abstract T get(int id);

    public abstract void delete(int id);

    public boolean exists(int id) {
        return get(id) != null;
    }

    public void wipe() {
        list().forEach(this::delete);
    }

    public abstract List<Integer> list();
}
