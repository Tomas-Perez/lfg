package persistence.manager.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * @author Tomas Perez Molina
 */
public class ManagerUtil {

    public static void persist(EntityManager manager, Object entity){
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
}
