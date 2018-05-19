package persistence.model;

import persistence.entity.*;
import persistence.manager.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class ModelBuilder {

    @Inject private GameManager gameManager;
    @Inject private UserManager userManager;
    @Inject private PostManager postManager;
    @Inject private ActivityManager activityManager;
    @Inject private GroupManager groupManager;

    private Activity huskActivity(int activityID){
        ActivityEntity activityEntity = activityManager.get(activityID);
        if(activityEntity == null) throw new NoSuchElementException();

        return new Activity(
                activityEntity,
                null
        );
    }

    public Activity buildActivity(int activityID){
        ActivityEntity activityEntity = activityManager.get(activityID);
        if(activityEntity == null) throw new NoSuchElementException();

        return new Activity(
                activityEntity,
                huskGame(activityEntity.getGameId())
        );
    }

    private Game huskGame(int gameID){
        GameEntity gameEntity = gameManager.get(gameID);
        if(gameEntity == null) throw new NoSuchElementException();

        return new Game(gameEntity.getId(), gameEntity.getName(), gameEntity.getImage(), null);
    }

    public Game buildGame(int gameID){
        GameEntity gameEntity = gameManager.get(gameID);
        if(gameEntity == null) throw new NoSuchElementException();

        Set<Activity> activities = gameManager
                .getGameActivities(gameEntity.getId())
                .stream()
                .map(this::huskActivity)
                .collect(Collectors.toSet());

        return new Game(
                gameEntity.getId(),
                gameEntity.getName(),
                gameEntity.getImage(),
                activities
        );
    }

    public User buildUser(int userID){
        UserEntity userEntity = userManager.get(userID);
        if(userEntity == null) throw new NoSuchElementException();

        Set<Group> groups = userManager
                .getUserGroups(userID)
                .stream()
                .map(this::huskGroup)
                .collect(Collectors.toSet());

        Set<Game> games = userManager
                .getUserGames(userID)
                .stream()
                .map(this::huskGame)
                .collect(Collectors.toSet());

        Set<User> friends = userManager
                .getUserFriends(userID)
                .stream()
                .map(this::huskUser)
                .collect(Collectors.toSet());

        Set<User> sentRequests = userManager
                .getSentRequests(userID)
                .stream()
                .map(this::huskUser)
                .collect(Collectors.toSet());

        Set<User> receivedRequests = userManager
                .getReceivedRequests(userID)
                .stream()
                .map(this::huskUser)
                .collect(Collectors.toSet());


        return new User(
                userEntity,
                groups,
                games,
                friends,
                sentRequests,
                receivedRequests
        );
    }

    private Group huskGroup(int groupID){
        GroupEntity groupEntity = groupManager.get(groupID);
        if(groupEntity == null) throw new NoSuchElementException();

        User owner = huskUser(groupManager.getGroupOwner(groupEntity.getId()));

        return new Group(
                groupEntity,
                buildActivity(groupEntity.getActivityId()),
                owner,
                null,
                null,
                null
        );
    }

    public Group buildGroup(int groupID){
        GroupEntity groupEntity = groupManager.get(groupID);
        if(groupEntity == null) throw new NoSuchElementException();

        Set<User> members = groupManager
                .getGroupMembers(groupID)
                .stream()
                .map(this::huskUser)
                .collect(Collectors.toSet());

        User owner = huskUser(groupManager.getGroupOwner(groupEntity.getId()));

        return new Group(
                groupEntity,
                buildActivity(groupEntity.getActivityId()),
                owner,
                null,
                null,
                members
        );
    }

    private User huskUser(int userID){
        UserEntity userEntity = userManager.get(userID);
        if(userEntity == null) throw new NoSuchElementException();

        return new User(
                userEntity,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Post buildPost(int postID){
        PostEntity postEntity = postManager.get(postID);
        if(postEntity == null) throw new NoSuchElementException();

        Activity activity = buildActivity(postEntity.getActivityId());
        User owner = huskUser(postEntity.getOwnerId());

        final Integer groupId = postEntity.getGroupId();
        Group group = null;
        if(groupId != null){
            group = buildGroup(groupId);
        }


        return new Post(
                postEntity,
                activity,
                owner,
                group,
                null,
                null
        );
    }
}
