package persistence.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import javax.persistence.PersistenceException;

/**
 * CDI producer for the JPA {@link EntityManager}.
 *
 */
@ApplicationScoped
public class EntityManagerProducer {
    private static Logger logger = LogManager.getLogger(EntityManagerProducer.class);

    private EntityManagerFactory factory;

    @PostConstruct
    public void init() {
        logger.debug("Initiating EntityManagerProducer");
        String unitName = "test2";
        logger.debug("Unit name: " + unitName);
        try {
            factory = Persistence.createEntityManagerFactory(unitName);
        } catch (PersistenceException exc){
            logger.fatal("Cannot connect to database", exc);
        }
    }

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
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
        factory.createEntityManager().close();
    }
}