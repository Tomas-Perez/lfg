package util;

import persistence.manager.GameManager;
import persistence.manager.UserManager;
import persistence.manager.EntityManagerProducer;
import persistence.manager.generator.KeyGenerator;
import persistence.model.Game;
import persistence.model.Key;

import javax.persistence.EntityManager;
import java.util.Random;

/**
 * @author Tomas Perez Molina
 */
public class JPAMain {


    public static void main(String[] args) throws Exception{
        EntityManagerProducer emp = new EntityManagerProducer();
        emp.init();
        final EntityManager entityManager = emp.createEntityManager();
        final KeyGenerator keyGenerator = new KeyGenerator(entityManager);
        final GameManager gm = new GameManager(entityManager, keyGenerator);

        int id = gm.addGame("Overwatch", null);
        Game game = gm.getGame(id);
        System.out.println(game);

        int id2 = gm.addGame("Overwatch2", null);
        Game game2 = gm.getGame(id2);
        System.out.println(game2);

        int id3 = gm.addGame("Overwatch3", null);
        Game game3 = gm.getGame(id3);
        System.out.println(game3);
        emp.closeEntityManager(entityManager);
        emp.destroy();

//        Random random = new Random();
//        for(int i = 0; i < 100; i += random.nextInt(9) + 1) {
//            System.out.println(String.format("%010d", i));
//            Thread.sleep(100);
//        }
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
