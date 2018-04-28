package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserManager {

    private EntityManager manager;

    @Inject
    public UserManager(EntityManager manager) {
        this.manager = manager;
        System.out.println("Initiating User manager");
    }

    public UserManager(){ }

    /* Method to CREATE an User in the database */
    public void addUser(@NotNull String username,
                        @NotNull String password,
                        @NotNull String email,
                        boolean isAdmin) throws ConstraintException
    {
        EntityTransaction tx = manager.getTransaction();

        try {
            tx.begin();
            User user = new User(username, password, email, isAdmin);
            manager.persist(user);
            tx.commit();
        } catch (PersistenceException e){
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            throw new ConstraintException(e);
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<User> listUsers(){
        return manager.createQuery("FROM User").getResultList();
    }

    /* Method to UPDATE password for an User */
    public void updateUser(int userID, @NotNull String password){
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


    public void deleteUser(int userID){
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

    public Optional<User> getByEmail(@NotNull String email){
        List<User> users = manager
                .createQuery("FROM User U WHERE U.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return users.stream().findFirst();
    }
}
