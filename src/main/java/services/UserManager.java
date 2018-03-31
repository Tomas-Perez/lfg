package services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import model.User;

import java.util.Iterator;
import java.util.List;

public class UserManager {


    private SessionFactory factory;

    public UserManager(SessionFactory factory){
        this.factory = factory;
    }

    /* Method to CREATE an User in the database */
    public Integer addUser(String username, String password, String email, boolean isAdmin){
        Session session = factory.openSession();
        Transaction tx = null;
        Integer userID = null;

        try {
            tx = session.beginTransaction();
            User user = new User(username, password, email, isAdmin);
            userID = (Integer) session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userID;
    }

    /* Method to  READ all the Users */
    public void listUsers( ){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List Users = session.createQuery("FROM User").list();
            for (Iterator iterator = Users.iterator(); iterator.hasNext();){
                User user = (User) iterator.next();
                System.out.print("Id: " + user.getUsername());
                System.out.print("Username: " + user.getUsername());
                System.out.print("Password: " + user.getPassword());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Is admin: " + user.isIsadmin());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to UPDATE password for an User */
    public void updateUser(Integer userID, String password ){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            User User = (User) session.get(User.class, userID);
            User.setPassword(password);
            session.update(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to DELETE an User from the records */
    public void deleteUser(Integer UserID){
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            User User = (User)session.get(User.class, UserID);
            session.delete(User);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
