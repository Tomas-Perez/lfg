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
    private static SessionFactory factory;

    private void buildFactory(){
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String[] args) {

        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        UserManager ME = new UserManager();


        /* Add few User records in database */
        Integer userID1 = ME.addUser("admin", "123", "admin@lfg.com", true);
        Integer userID2 = ME.addUser("User1", "pass1", "user1@lfg.com", false);
        Integer userID3 = ME.addUser("User2", "pass2", "user2@lfg.com", false);

        /* List down all the Users */
        ME.listUsers();

        /* Update User's records */
        //ME.updateUser(userID1, "PASS1");

        /* Delete an User from the database */

        /* List down new list of the Users */
        ME.listUsers();

        factory.close();
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
