package services;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import model.User;

public class UserManager {


    private SessionFactory factory;

    public UserManager(SessionFactory factory){
        this.factory = factory;
    }

    /* Method to CREATE an User in the database */
    public Integer addUser(String username, String password, String email, boolean isAdmin){
        Transaction tx = null;
        Integer userID = null;

        try (var session = factory.openSession()) {
            tx = session.beginTransaction();
            var user = new User(username, password, email, isAdmin);
            userID = (Integer) session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return userID;
    }

    /* Method to  READ all the Users */
    public void listUsers( ){
        Transaction tx = null;
        try {
            var session = factory.getCurrentSession();
            tx = session.beginTransaction();
            var Users = session.createQuery("FROM User").list();
            for (var User : Users) {
                var user = (User) User;
                System.out.print("Id: " + user.getUsername());
                System.out.print(" Username: " + user.getUsername());
                System.out.print(" Password: " + user.getPassword());
                System.out.println(" Email: " + user.getEmail());
                System.out.println("Is admin: " + user.isIsadmin());
                System.out.println("------------------------");
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to UPDATE password for an User */
    public void updateUser(Integer userID, String password ){
        Transaction tx = null;

        try (var session = factory.openSession()) {
            tx = session.beginTransaction();
            var User = session.get(User.class, userID);
            User.setPassword(password);
            session.update(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to DELETE an User from the records */
    public void deleteUser(Integer UserID){
        Transaction tx = null;

        try (var session = factory.openSession()) {
            tx = session.beginTransaction();
            var User = session.get(User.class, UserID);
            session.delete(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
