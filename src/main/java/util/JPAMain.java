package util;

import persistence.entity.GameEntity;
import persistence.entity.OwnsGameEntity;
import persistence.entity.OwnsGameEntityPK;
import persistence.entity.UserEntity;
import persistence.manager.EntityManagerProducer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * @author Tomas Perez Molina
 */
public class JPAMain {


    public static void main(String[] args) throws Exception{
        EntityManagerProducer emp = new EntityManagerProducer();
        emp.init();
        EntityManager manager = emp.createEntityManager();
        GameEntity gameEntity = new GameEntity();
        gameEntity.setId(1);
        gameEntity.setImage(null);
        gameEntity.setName("Overwatch");

        UserEntity userEntity = new UserEntity();
        userEntity.setAdmin(true);
        userEntity.setEmail("email");
        userEntity.setId(1);
        userEntity.setPassword("password");
        userEntity.setUsername("username");

        OwnsGameEntity ownsGameEntity = new OwnsGameEntity();
        ownsGameEntity.setGameId(1);
        ownsGameEntity.setOwnerId(1);

        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.persist(gameEntity);
            manager.persist(userEntity);
            manager.persist(ownsGameEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        OwnsGameEntityPK key = new OwnsGameEntityPK();
        key.setGameId(1);
        key.setOwnerId(1);
        System.out.println(manager.find(OwnsGameEntity.class, key));

        tx = manager.getTransaction();
        GameEntity ge = manager.find(GameEntity.class, 1);
        try {
            tx.begin();
            manager.remove(ge);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        System.out.println("BEFORE CLOSE: " + manager.find(OwnsGameEntity.class, key));
        emp.closeEntityManager(manager);

        manager = emp.createEntityManager();
        System.out.println("AFTER CLOSE: " + manager.find(OwnsGameEntity.class, key));
//        final KeyGenerator keyGenerator = new KeyGenerator(entityManager);
//        final GameManager gm = new GameManager(entityManager, keyGenerator);
//
//        int id = gm.addGame("Overwatch", null);
//        Game game = gm.getGame(id);
//        System.out.println(game);
//
//        int id2 = gm.addGame("Overwatch2", null);
//        Game game2 = gm.getGame(id2);
//        System.out.println(game2);
//
//        int id3 = gm.addGame("Overwatch3", null);
//        Game game3 = gm.getGame(id3);
//        System.out.println(game3);
        emp.closeEntityManager(manager);
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
