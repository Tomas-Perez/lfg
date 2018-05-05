package persistence.manager;

import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.manager.generator.KeyGenerator;
import persistence.manager.patcher.UserPatcher;
import persistence.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationScoped
public class UserManager {

    private EntityManager manager;
    private KeyGenerator generator;

    @Inject
    public UserManager(EntityManager manager, KeyGenerator generator) {
        this.manager = manager;
        this.generator = generator;
    }

    public UserManager(){ }

    public int addUser(@NotNull String username,
                        @NotNull String password,
                        @NotNull String email,
                        boolean isAdmin) throws ConstraintException
    {
        checkValidCreation(username, email);
        EntityTransaction tx = manager.getTransaction();
        int id = generator.generate("user");
        User user = new User(id, username, password, email, isAdmin);

        try {
            tx.begin();
            manager.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        return user.getId();
    }

    @SuppressWarnings("unchecked")
    public List<User> listUsers(){
        return manager.createQuery("FROM User").getResultList();
    }

    public void updateUser(int userID, @NotNull UserPatcher patcher) throws ConstraintException{
        checkValidUpdate(patcher);
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            User user = manager.find(User.class, userID);
            patcher.patch(user);
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
        } catch (NullPointerException | IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
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

    private void checkValidCreation(@NotNull String username, @NotNull String email) throws ConstraintException{
        if(userExistsByUserName(username)) throw new ConstraintException(username);
        if(userExistsByEmail(email)) throw new ConstraintException(email);
    }

    private void checkValidUpdate(@NotNull UserPatcher patcher) throws ConstraintException{
        if(patcher.patchesUsername() && userExistsByUserName(patcher.getUsername()))
            throw new ConstraintException(patcher.getUsername());

        if(patcher.patchesEmail() && userExistsByEmail(patcher.getEmail()))
            throw new ConstraintException(patcher.getEmail());
    }

    public boolean userExistsByUserName(@NotNull String username){
        return manager
                .createQuery("SELECT 1 FROM User U WHERE U.username = :username")
                .setParameter("username", username)
                .getResultList().size() > 0;
    }

    public boolean userExistsByEmail(@NotNull String email){
        return manager
                .createQuery("SELECT 1 FROM User U WHERE U.email = :email")
                .setParameter("email", email)
                .getResultList().size() > 0;
    }

    public void wipeAllRecords(){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            manager.createQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public User getUser(int userID){
        return manager.find(User.class, userID);
    }
}
