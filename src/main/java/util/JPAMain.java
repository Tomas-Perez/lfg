package util;

import model.User;
import restapi.common.persistence.EntityManagerProducer;

import javax.persistence.EntityManager;

/**
 * @author Tomas Perez Molina
 */
public class JPAMain {
    public static void main(String[] args) {
        EntityManagerProducer emp = new EntityManagerProducer();
        emp.init();
        EntityManager em = emp.createEntityManager();
        User user = em.find(User.class, 19);

        System.out.println(user);
        emp.destroy();
    }
}
