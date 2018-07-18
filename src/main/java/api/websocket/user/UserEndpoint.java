package api.websocket.user;

import api.common.event.chat.ChatEvent;
import api.common.event.chat.DeleteChat;
import api.common.event.chat.NewChat;
import api.common.event.friendrequest.*;
import api.common.event.group.DeleteGroup;
import api.common.event.group.GroupEvent;
import api.common.event.group.NewGroup;
import api.common.event.post.*;
import api.websocket.common.AuthenticatedEndpoint;
import api.websocket.common.config.CdiAwareConfigurator;
import api.websocket.common.model.Payload;
import api.websocket.user.codec.UserMessageDecoder;
import api.websocket.user.codec.UserMessageEncoder;
import api.websocket.user.model.UserSocketMessage;
import api.websocket.user.model.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.manager.EntityManagerProducer;
import persistence.manager.FriendHelperManager;
import persistence.manager.UserManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/user",
        encoders = UserMessageEncoder.class,
        decoders = UserMessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class UserEndpoint extends AuthenticatedEndpoint {
    private static final Map<Integer, Session> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(UserEndpoint.class);

    @Inject
    private EntityManagerProducer emp;

    @OnOpen
    public void onOpen(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.put(userID, currentSession);
        sendFriendsConnected(userID);
        broadcastUserConnected(userID);

        logger.info(String.format("Session %s opened", currentSession.getId()));
    }

    @OnClose
    public void onClose(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.remove(userID);
        broadcastUserDisconnected(userID);

        logger.info(String.format("Session %s closed", currentSession.getId()));
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        logger.error(throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session currentSession, UserSocketMessage message) {}

    private void broadcastUserConnected(int id) {
        FriendConnectedPayload payload = new FriendConnectedPayload(id);
        Set<Integer> userFriends = getUserFriends(id);
        broadcastTo(payload, userFriends);
        logger.info(String.format("User %d connected", id));
    }

    private void broadcastUserDisconnected(int id) {
        FriendDisconnectedPayload payload = new FriendDisconnectedPayload(id);
        Set<Integer> userFriends = getUserFriends(id);
        broadcastTo(payload, userFriends);
        logger.info(String.format("User %d disconnected", id));
    }

    private void broadcast(Payload payload, int ignoreID){
        UserSocketMessage msg = new UserSocketMessage(payload);

        synchronized (sessionsMap) {
            sessionsMap.entrySet().stream()
                    .filter(e -> e.getKey() != ignoreID)
                    .filter(e -> e.getValue().isOpen())
                    .map(Map.Entry::getValue)
                    .forEach(session -> session.getAsyncRemote().sendObject(msg));
            logger.info(String.format("Broadcasting \"%s\" to all sessions except session of user %d", msg, ignoreID));
        }
    }

    private void broadcastTo(Payload payload, Set<Integer> set){
        UserSocketMessage msg = new UserSocketMessage(payload);

        synchronized (sessionsMap) {
            sessionsMap.entrySet().stream()
                    .filter(e -> set.contains(e.getKey()))
                    .filter(e -> e.getValue().isOpen())
                    .map(Map.Entry::getValue)
                    .forEach(session -> session.getAsyncRemote().sendObject(msg));
            logger.info(String.format("Broadcasting \"%s\" to %s", msg, set));
        }
    }

    private void sendMessage(Payload payload, int userID){
        UserSocketMessage msg = new UserSocketMessage(payload);

        Session session = sessionsMap.get(userID);
        if(session != null) {
            session.getAsyncRemote().sendObject(msg);
            logger.info(String.format("Sending \"%s\" to user %d", msg, userID));
        }
    }

    private void newFriendRequest(@Observes @FriendRequest FriendRequestEvent event){
        ReceivedFriendRequestPayload receivedFriendRequestPayload
                = new ReceivedFriendRequestPayload(event.getSender());
        SentFriendRequestPayload sentFriendRequestPayload
                = new SentFriendRequestPayload(event.getReceiver());

        sendMessage(receivedFriendRequestPayload, event.getReceiver().getId());
        sendMessage(sentFriendRequestPayload, event.getSender().getId());
    }

    private void newFriend(@Observes @NewFriend FriendEvent event){
        NewFriendPayload friend1Payload = new NewFriendPayload(event.getUser1());
        NewFriendPayload friend2Payload = new NewFriendPayload(event.getUser2());

        final int user2ID = event.getUser2().getId();
        final int user1ID = event.getUser1().getId();

        sendMessage(friend1Payload, user2ID);
        sendMessage(friend2Payload, user1ID);

        notifyNewFriendConnection(user1ID, user2ID);
        notifyNewFriendConnection(user2ID, user1ID);
    }

    private void deleteFriend(@Observes @DeleteFriend FriendEvent event){
        final int friend1ID = event.getUser1().getId();
        final int friend2ID = event.getUser2().getId();

        DeleteFriendPayload friend1Payload = new DeleteFriendPayload(friend1ID);
        DeleteFriendPayload friend2Payload = new DeleteFriendPayload(friend2ID);

        sendMessage(friend1Payload, friend2ID);
        sendMessage(friend2Payload, friend1ID);
    }

    private void newChat(@Observes @NewChat ChatEvent event){
        NewChatPayload newChatPayload = new NewChatPayload(event.getId());
        broadcastTo(newChatPayload, event.getNotifySet());
    }

    private void deleteChat(@Observes @DeleteChat ChatEvent event){
        DeleteChatPayload deleteChatPayload = new DeleteChatPayload(event.getId());
        broadcastTo(deleteChatPayload, event.getNotifySet());
    }

    private void newPost(@Observes @NewPost PostEvent event){
        NewPostPayload payload = new NewPostPayload(event.getPostID());
        sendMessage(payload, event.getOwnerID());
    }

    private void deletePost(@Observes @DeletePost PostEvent event){
        DeletePostPayload payload = new DeletePostPayload(event.getPostID());
        sendMessage(payload, event.getOwnerID());
    }

    private void newGroupPost(@Observes @NewGroupPost GroupPostEvent event){
        NewGroupPostPayload payload = new NewGroupPostPayload(event.getPostID());
        broadcastTo(payload, event.getNotifySet());
    }

    private void deleteGroupPost(@Observes @DeleteGroupPost GroupPostEvent event){
        DeleteGroupPostPayload payload = new DeleteGroupPostPayload(event.getPostID());
        broadcastTo(payload, event.getNotifySet());
    }

    private void updatePost(@Observes @UpdatePost UpdatePostEvent event){
        UpdatePostPayload payload = new UpdatePostPayload(event.getPostID());
        broadcastTo(payload, event.getNotifySet());
    }

    private void newGroup(@Observes @NewGroup GroupEvent event){
        NewGroupPayload payload = new NewGroupPayload(event.getId());
        broadcastTo(payload, event.getNotifySet());
    }

    private void deleteGroup(@Observes @DeleteGroup GroupEvent event){
        DeleteGroupPayload payload = new DeleteGroupPayload(event.getId());
        broadcastTo(payload, event.getNotifySet());
    }

    private Set<Integer> getUserFriends(int id){
        EntityManager em = emp.createEntityManager();
        UserManager userManager = new UserManager(em, new FriendHelperManager());
        Set<Integer> friends = userManager.getUserFriends(id);
        emp.closeEntityManager(em);
        return friends;
    }

    private void sendFriendsConnected(int userID){
        Set<Integer> friends = getUserFriends(userID);
        Set<FriendConnectedPayload> connectedFriends;
        synchronized (sessionsMap) {
            connectedFriends = sessionsMap.entrySet().stream()
                    .filter(e -> friends.contains(e.getKey()))
                    .filter(e -> e.getValue().isOpen())
                    .map(Map.Entry::getKey)
                    .map(FriendConnectedPayload::new)
                    .collect(Collectors.toSet());
        }
        if(connectedFriends == null) return;
        connectedFriends.forEach(payload -> sendMessage(payload, userID));
    }

    private void notifyNewFriendConnection(int userID, int newFriendID){
        if(sessionsMap.get(userID) != null){
            FriendConnectedPayload connectedPayload = new FriendConnectedPayload(userID);
            sendMessage(connectedPayload, newFriendID);
        }
    }
}
