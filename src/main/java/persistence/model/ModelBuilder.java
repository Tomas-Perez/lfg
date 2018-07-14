package persistence.model;

import persistence.entity.*;
import persistence.manager.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
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
    @Inject private ChatManager chatManager;
    @Inject private ChatPlatformManager chatPlatformManager;
    @Inject private GamePlatformManager gamePlatformManager;

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

        Set<Chat> chats = userManager
                .getUserChats(userID)
                .stream()
                .map(this::huskChat)
                .collect(Collectors.toSet());

        Integer postID = userManager
                .getUserPost(userID);
        Post post = postID == null? null : huskPost(postID);


        return new User(
                userEntity,
                groups,
                games,
                friends,
                sentRequests,
                receivedRequests,
                chats,
                post
        );
    }

    private Group huskGroup(int groupID){
        GroupEntity groupEntity = groupManager.get(groupID);
        if(groupEntity == null) throw new NoSuchElementException();

        User owner = huskUser(groupManager.getGroupOwner(groupEntity.getId()));
        Chat chat = huskChat(groupManager.getGroupChat(groupID));

        return new Group(
                groupEntity,
                buildActivity(groupEntity.getActivityId()),
                owner,
                null,
                null,
                null,
                chat
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
        Chat chat = huskChat(groupManager.getGroupChat(groupID));

        final Integer gamePlatformId = groupEntity.getGamePlatformId();
        GamePlatform gamePlatform = gamePlatformId == null? null : buildGamePlatform(gamePlatformId);
        final Integer chatPlatformId = groupEntity.getChatPlatformId();
        ChatPlatform chatPlatform = chatPlatformId == null? null : buildChatPlatform(chatPlatformId);

        return new Group(
                groupEntity,
                buildActivity(groupEntity.getActivityId()),
                owner,
                chatPlatform,
                gamePlatform,
                members,
                chat
        );
    }

    public User huskUser(int userID){
        UserEntity userEntity = userManager.get(userID);
        if(userEntity == null) throw new NoSuchElementException();

        return new User(
                userEntity,
                null,
                null,
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

        final Integer groupID = postEntity.getGroupId();
        Group group;

        Set<GamePlatform> gamePlatforms;
        Set<ChatPlatform> chatPlatforms;

        if(groupID == null) {
            group = null;

            gamePlatforms = postManager
                    .getPostGamePlatforms(postID)
                    .stream()
                    .map(this::buildGamePlatform)
                    .collect(Collectors.toSet());

            chatPlatforms = postManager
                    .getPostChatPlatforms(postID)
                    .stream()
                    .map(this::buildChatPlatform)
                    .collect(Collectors.toSet());

        } else {
            group = buildGroup(groupID);
            final GamePlatform gamePlatform = group.getGamePlatform();
            gamePlatforms = gamePlatform == null? new HashSet<>() : Collections.singleton(gamePlatform);
            final ChatPlatform chatPlatform = group.getChatPlatform();
            chatPlatforms = chatPlatform == null? new HashSet<>() : Collections.singleton(chatPlatform);
        }

        return new Post(
                postEntity,
                activity,
                owner,
                group,
                chatPlatforms,
                gamePlatforms
        );
    }

    private Post huskPost(int postID){
        PostEntity postEntity = postManager.get(postID);
        if(postEntity == null) throw new NoSuchElementException();

        Activity activity = buildActivity(postEntity.getActivityId());

        return new Post(
                postEntity,
                activity,
                null,
                null,
                new HashSet<>(),
                new HashSet<>()
        );
    }

    public Chat buildChat(int chatID){
        ChatEntity chatEntity = chatManager.get(chatID);
        if(chatEntity == null) throw new NoSuchElementException();

        List<Message> messages = chatManager
                .getChatMessages(chatID)
                .stream()
                .map(this::buildMessage)
                .collect(Collectors.toList());

        Set<User> members = chatManager
                .getChatMembers(chatID)
                .stream()
                .map(this::huskUser)
                .collect(Collectors.toSet());

        return new Chat(chatEntity, members, messages);
    }

    private Chat huskChat(int chatID){
        ChatEntity chatEntity = chatManager.get(chatID);
        if(chatEntity == null) throw new NoSuchElementException();

        return new Chat(chatEntity, null, null);
    }

    public Message buildMessage(int msgID){
        ChatMessageEntity messageEntity = chatManager.getMessage(msgID);
        if(messageEntity == null) throw new NoSuchElementException();

        User sender = huskUser(messageEntity.getSenderId());

        return new Message(messageEntity, sender);
    }

    public GamePlatform buildGamePlatform(int gamePlatformID){
        GamePlatformEntity entity = gamePlatformManager.get(gamePlatformID);
        if(entity == null) throw new NoSuchElementException();

        return new GamePlatform(entity);
    }

    public ChatPlatform buildChatPlatform(int chatPlatformID){
        ChatPlatformEntity entity = chatPlatformManager.get(chatPlatformID);
        if(entity == null) throw new NoSuchElementException();

        return new ChatPlatform(entity);
    }
}
