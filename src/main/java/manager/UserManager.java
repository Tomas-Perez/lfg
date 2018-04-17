package manager;

import model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserManager {

    @Inject
    private EntityManager manager;

    public UserManager() {
        System.out.println("Initiating User manager");
    }

    /* Method to CREATE an User in the database */
    public void addUser(String username, String password, String email, boolean isAdmin) throws ConstraintException {
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            User user = new User(username, password, email, isAdmin);
            manager.persist(user);
            tx.commit();
        } catch (PersistenceException e){
            if (tx!=null) tx.rollback();
            throw new ConstraintException(e);
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to  READ all the Users */
    public void listUsers(){
        List<?> users = manager.createQuery("FROM User").getResultList();
        users.forEach(System.out::println);
    }

    /* Method to UPDATE password for an User */
    public void updateUser(Integer userID, String password){
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            User user = manager.find(User.class, userID);
            user.setPassword(password);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to DELETE an User from the records */
    public void deleteUser(Integer userID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            User user = manager.find(User.class, userID);
            manager.remove(user);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Optional<User> getByEmail(String email){
        System.out.println(manager);
        List<User> users = manager.createQuery("FROM User U WHERE U.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }
}
