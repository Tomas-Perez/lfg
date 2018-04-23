package util;

import persistence.manager.UserManager;
import persistence.manager.EntityManagerProducer;

import javax.persistence.EntityManager;

/**
 * @author Tomas Perez Molina
 */
public class JPAMain {


    public static void main(String[] args) {
        EntityManagerProducer emp = new EntityManagerProducer();
        emp.init();
        EntityManager em = emp.createEntityManager();
        UserManager manager = new UserManager(em);
        System.out.println(manager.getByEmail("wewey@lfg.com").get());
        emp.closeEntityManager(em);
        emp.destroy();
//        EntityManagerProducer emp = new EntityManagerProducer();
//        emp.init();
//        EntityManager em = emp.createEntityManager();
//        User user = em.find(User.class, 19);
//
//        System.out.println(user);
//        emp.destroy();
    }
}
