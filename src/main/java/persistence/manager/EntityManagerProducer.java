package persistence.manager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * CDI producer for the JPA {@link EntityManager}.
 *
 */
@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory factory;

    @PostConstruct
    public void init() {
        System.out.println("Initiating EntityManagerProducer");
        String test2 = "test2";
        System.out.println(test2);
        factory = Persistence.createEntityManagerFactory(test2);
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        System.out.println("Creating Entity Manager");
        return factory.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    @PreDestroy
    public void destroy() {
        if (factory.isOpen()) {
            factory.close();
        }
    }

    //Eager factory instantiation
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("QUICKLY");
        factory.createEntityManager().close();
    }
}