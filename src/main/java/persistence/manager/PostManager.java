package persistence.manager;

import common.postfilter.FilterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import persistence.manager.exception.ConstraintException;
import persistence.entity.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class PostManager extends Manager<PostEntity>{
    private static final Logger logger = LogManager.getLogger(PostManager.class);

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
        checkValidCreation(post, new HashSet<>(), new HashSet<>());
        persist(post);
        return post.getId();
    }

    public int add(
            @NotNull PostEntity post,
            @NotNull Set<Integer> chatPlatformIDs,
            @NotNull Set<Integer> gamePlatformIDs)
    {
        checkValidCreation(post, chatPlatformIDs, gamePlatformIDs);
        persist(post);
        userManager.setLastPosted(post.getOwnerId(), LocalDateTime.now());
        final int postID = post.getId();
        gamePlatformIDs.stream()
                .map(id -> new GamePlatformForPostEntity(postID, id))
                .forEach(this::persist);
        chatPlatformIDs.stream()
                .map(id -> new ChatPlatformForPostEntity(postID, id))
                .forEach(this::persist);
        return postID;
    }

    public int addGroupPost(@NotNull String description,
                            @NotNull LocalDateTime date,
                            @NotNull GroupEntity group,
                            @NotNull Set<Integer> chatPlatformIDs,
                            @NotNull Set<Integer> gamePlatformIDs)
    {
        Integer groupOwner = groupManager.getGroupOwner(group.getId());
        PostEntity post = new PostEntity(
                description,
                date,
                group.getActivityId(),
                groupOwner,
                group.getId()
        );
        checkValidCreation(post, chatPlatformIDs, gamePlatformIDs);
        persist(post);
        userManager.setLastPosted(post.getOwnerId(), LocalDateTime.now());
        final int postID = post.getId();

        gamePlatformIDs.stream()
                .map(id -> new GamePlatformForPostEntity(postID, id))
                .forEach(this::persist);
        chatPlatformIDs.stream()
                .map(id -> new ChatPlatformForPostEntity(postID, id))
                .forEach(this::persist);

        return postID;
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
    public List<Integer> filteredList(FilterData filter){
        logger.info(filter);
        final List resultList = manager.createNativeQuery("SELECT P.id FROM POST P " +
                "LEFT JOIN GAME_PLATFORM_FOR_POST G on G.POST_ID = P.id " +
                "LEFT JOIN CHAT_PLATFORM_FOR_POST C on C.POST_ID = P.id " +
                "JOIN ACTIVITY A on A.id = P.ACTIVITY_ID " +
                "WHERE (A.GAME_ID = :gameID OR :gameID is null) " +
                "AND (A.ID = :activityID OR :activityID is null) " +
                "AND (G.GAME_PLATFORM_ID = :gamePlatformID OR :gamePlatformID is null) " +
                "AND (C.CHAT_PLATFORM_ID = :chatPlatformID OR :chatPlatformID is null) " +
                "AND ((P.GROUP_ID is not null) = :hasGroup) " +
                "ORDER BY P.date DESC")
                .setParameter("gameID", filter.getGameID())
                .setParameter("activityID", filter.getActivityID())
                .setParameter("chatPlatformID", filter.getChatPlatformID())
                .setParameter("gamePlatformID", filter.getGamePlatformID())
                .setParameter("hasGroup", filter.getType() == FilterData.PostType.LFM)
                .getResultList();
        logger.info(resultList);
        return resultList;

//        if(filter.getGameID() == null) return list();
//        if(filter.getActivityID() == null) return getGamePosts(filter.getGameID());
//        return getGameActivityPosts(filter.getGameID(), filter.getActivityID());
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

    public Integer getUserPost(int userID){
        return userManager.getUserPost(userID);
    }

    public LocalDateTime getUserLastPosted(int userID){
        return userManager.getLastPosted(userID);
    }

    public PostEntity get(int postID){
        return manager.find(PostEntity.class, postID);
    }

    private void checkValidCreation(PostEntity post, Set<Integer> chatPlatformIDs, Set<Integer> gamePlatformIDs){
        userManager.checkExistence(post.getOwnerId());
        activityManager.checkExistence(post.getActivityId());
        gamePlatformIDs.forEach(gamePlatformManager::checkExistence);
        chatPlatformIDs.forEach(chatPlatformManager::checkExistence);
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

    @SuppressWarnings("unchecked")
    public Set<Integer> getPostGamePlatforms(int postID){
        return (Set<Integer>) new HashSet<>(manager.createQuery("SELECT G.gamePlatformId " +
                "FROM GamePlatformForPostEntity G " +
                "WHERE G.postId = :postID")
                .setParameter("postID", postID)
                .getResultList());
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> getPostChatPlatforms(int postID){
        return (Set<Integer>) new HashSet<>(manager.createQuery("SELECT C.chatPlatformId " +
                "FROM ChatPlatformForPostEntity C " +
                "WHERE C.postId = :postID")
                .setParameter("postID", postID)
                .getResultList());
    }

    public Integer getPostGroup(int postID){
        final PostEntity postEntity = get(postID);
        return postEntity == null? null : postEntity.getGroupId();
    }
}
