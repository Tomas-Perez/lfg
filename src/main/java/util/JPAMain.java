package util;

import persistence.manager.UserManager;
import persistence.manager.EntityManagerProducer;

import javax.persistence.EntityManager;
import java.util.Random;

/**
 * @author Tomas Perez Molina
 */
public class JPAMain {


    public static void main(String[] args) throws Exception{
        Random random = new Random();
        for(int i = 0; i < 100; i += random.nextInt(9) + 1) {
            System.out.println(String.format("%010d", i));
            Thread.sleep(100);
        }
//        EntityManagerProducer emp = new EntityManagerProducer();
//        emp.init();
//        EntityManager em = emp.createEntityManager();
////        UserManager manager = new UserManager(em);
////        System.out.println(manager.getByEmail("wewey@lfg.com").get());
//        emp.closeEntityManager(em);
//        emp.destroy();
//        EntityManagerProducer emp = new EntityManagerProducer();
//        emp.init();
//        EntityManager em = emp.createEntityManager();
//        User user = em.find(User.class, 19);
//
//        System.out.println(user);
//        emp.destroy();
    }
}
