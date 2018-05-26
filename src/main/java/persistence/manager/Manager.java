package persistence.manager;

import persistence.manager.util.ManagerUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;

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
        ManagerUtil.persist(manager, entity);
    }

    protected void delete(Class clazz, int id){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            Object entity = manager.find(clazz, id);
            manager.remove(entity);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
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

    public abstract void checkExistence(int id);
}
