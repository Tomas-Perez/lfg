package managers;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import model.User;
import org.hibernate.query.Query;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class UserManager {


    private SessionFactory factory;

    public UserManager(SessionFactory factory){
        this.factory = factory;
    }

    /* Method to CREATE an User in the database */
    public Integer addUser(String username, String password, String email, boolean isAdmin) throws ConstraintException {
        Session session = factory.getCurrentSession();
        Transaction tx = null;
        Integer userID = null;

        try {
            tx = session.beginTransaction();
            User user = new User(username, password, email, isAdmin);
            userID = (Integer) session.save(user);
            tx.commit();
        } catch (ConstraintViolationException e){
            if (tx!=null) tx.rollback();
            throw new ConstraintException(e);
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        return userID;
    }

    /* Method to  READ all the Users */
    public void listUsers(){
        Session session = factory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<?> users = session.createQuery("FROM User").list();
            users.forEach(System.out::println);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to UPDATE password for an User */
    public void updateUser(Integer userID, String password){
        Session session = factory.getCurrentSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            User User = session.get(User.class, userID);
            User.setPassword(password);
            session.update(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    /* Method to DELETE an User from the records */
    public void deleteUser(Integer UserID){
        Session session = factory.getCurrentSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            User User = session.get(User.class, UserID);
            session.delete(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public User getByEmail(String email){
        Session session = factory.getCurrentSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            String query = "FROM User U WHERE U.email = :email";
            Query criteria = session.createQuery(query);
            criteria.setParameter("email", email);
            user = (User) criteria.getSingleResult();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        return user;
    }
}
