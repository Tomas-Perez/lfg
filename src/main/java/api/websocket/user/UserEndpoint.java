package api.websocket.user;

import api.common.event.chat.ChatEvent;
import api.common.event.chat.DeleteChat;
import api.common.event.chat.NewChat;
import api.common.event.friendrequest.*;
import api.websocket.common.AuthenticatedEndpoint;
import api.websocket.common.config.CdiAwareConfigurator;
import api.websocket.common.model.Payload;
import api.websocket.user.codec.ChatMessageDecoder;
import api.websocket.user.codec.ChatMessageEncoder;
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
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/user",
        encoders = ChatMessageEncoder.class,
        decoders = ChatMessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class UserEndpoint extends AuthenticatedEndpoint {
    private static final Map<Integer, Session> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Integer, Session> recentlyConnectedMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(UserEndpoint.class);

    @Inject
    private EntityManagerProducer emp;

    @OnOpen
    public void onOpen(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.put(userID, currentSession);
        if(recentlyConnectedMap.get(userID) == null)
            broadcastUserConnected(userID);

        logger.info(String.format("Session %s opened", currentSession.getId()));
    }

    @OnMessage
    public void onMessage(Session currentSession, UserSocketMessage message) {}

    @OnClose
    public void onClose(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.remove(userID);
        recentlyConnectedMap.put(userID, currentSession);

        Timer timer = new Timer("Connect Timer");

        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(sessionsMap.get(userID) == null) {
                            recentlyConnectedMap.remove(userID);
                            broadcastUserDisconnected(userID);
                            timer.cancel();
                        }
                    }
                },
                3000
        );

        logger.info(String.format("Session %s closed", currentSession.getId()));
    }

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

        sendMessage(friend1Payload, event.getUser2().getId());
        sendMessage(friend2Payload, event.getUser1().getId());
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

    private Set<Integer> getUserFriends(int id){
        EntityManager em = emp.createEntityManager();
        UserManager userManager = new UserManager(em, new FriendHelperManager());
        Set<Integer> friends = userManager.getUserFriends(id);
        emp.closeEntityManager(em);
        return friends;
    }
}
