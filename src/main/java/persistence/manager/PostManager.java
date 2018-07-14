package persistence.manager;

import common.postfilter.FilterPair;
import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class PostManager extends Manager<PostEntity>{
    private UserManager userManager;
    private ActivityManager activityManager;
    private GroupManager groupManager;
    private ChatPlatformManager chatPlatformManager;
    private GamePlatformManager gamePlatformManager;

    @Inject
    public PostManager(EntityManager manager,
                       UserManager userManager,
                       ActivityManager activityManager,
                       GroupManager groupManager,
                       ChatPlatformManager chatPlatformManager,
                       GamePlatformManager gamePlatformManager) {
        super(manager);
        this.userManager = userManager;
        this.activityManager = activityManager;
        this.groupManager = groupManager;
        this.chatPlatformManager = chatPlatformManager;
        this.gamePlatformManager = gamePlatformManager;
    }

    public PostManager(){}

    public int add(PostEntity post) {
        checkValidCreation(post.getOwnerId(), post.getActivityId());
        persist(post);
        return post.getId();
    }

    public int addGroupPost(String description,
                             @NotNull LocalDateTime date,
                             @NotNull GroupEntity group)
    {
        Integer groupOwner = groupManager.getGroupOwner(group.getId());
        PostEntity post = new PostEntity(
                description,
                date,
                group.getActivityId(),
                groupOwner,
                group.getId()
        );
        persist(post);

        return post.getId();
    }

    public void delete(int postID){
        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            PostEntity post = manager.find(PostEntity.class, postID);
            manager.remove(post);
            tx.commit();
        } catch (NullPointerException | IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Integer> list(){
        return manager.createQuery("SELECT P.id FROM PostEntity P ORDER BY P.date DESC").getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> filteredList(FilterPair filter){
        if(filter.getGameID() == null) return list();
        if(filter.getActivityID() == null) return getGamePosts(filter.getGameID());
        return getGameActivityPosts(filter.getGameID(), filter.getActivityID());
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGamePosts(int gameID){
        return manager.createQuery("SELECT P.id FROM PostEntity P " +
                "JOIN ActivityEntity A on A.id = P.activityId " +
                "WHERE A.gameId = :gameID " +
                "ORDER BY P.date DESC")
                .setParameter("gameID", gameID)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getGameActivityPosts(int gameID, int activityID){
        return manager.createQuery("SELECT P.id FROM PostEntity P " +
                "JOIN ActivityEntity A on A.id = P.activityId " +
                "WHERE A.gameId = :gameID AND A.id = :activityID " +
                "ORDER BY P.date DESC")
                .setParameter("gameID", gameID)
                .setParameter("activityID", activityID)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Integer getUserPost(int userID){
        return userManager.getUserPost(userID);
    }

    public PostEntity get(int postID){
        return manager.find(PostEntity.class, postID);
    }

    private void checkValidCreation(int ownerID, int activityID){
        userManager.checkExistence(ownerID);
        activityManager.checkExistence(activityID);
    }

    public void checkExistence(int postID){
        if(!exists(postID))
            throw new ConstraintException(String.format("Post with id: %d does not exist", postID));
    }

    public void addChatPlatformToPost(int chatPlatformID, int postID){
        addChatPlatformsToPost(postID, Collections.singleton(chatPlatformID));
    }

    public void addChatPlatformsToPost(int postID, Set<Integer> chatPlatformIDs){
        checkExistence(postID);
        chatPlatformIDs.forEach(chatPlatformManager::checkExistence);
        chatPlatformIDs.stream()
                .map(id -> new ChatPlatformForPostEntity(postID, id))
                .forEach(this::persist);
    }

    public void addGamePlatformToPost(int gamePlatformID, int postID){
        addGamePlatformsToPost(postID, Collections.singleton(gamePlatformID));
    }

    public void addGamePlatformsToPost(int postID, Set<Integer> gamePlatformIDs){
        checkExistence(postID);
        gamePlatformIDs.forEach(gamePlatformManager::checkExistence);
        gamePlatformIDs.stream()
                .map(id -> new GamePlatformForPostEntity(postID, id))
                .forEach(this::persist);
    }
}
