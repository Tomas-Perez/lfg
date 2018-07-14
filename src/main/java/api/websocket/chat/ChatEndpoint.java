package api.websocket.chat;

import api.common.event.chat.ChatEvent;
import api.common.event.chat.NewChat;
import api.rest.chat.model.MessageJSON;
import api.rest.user.model.BasicUserData;
import api.websocket.chat.codec.ChatMessageDecoder;
import api.websocket.chat.codec.ChatMessageEncoder;
import api.websocket.chat.model.ChatSocketMessage;
import api.websocket.chat.model.payload.*;
import api.websocket.common.AuthenticatedEndpoint;
import api.websocket.common.config.CdiAwareConfigurator;
import api.websocket.common.security.AuthenticatedPrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.manager.*;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/chats/{id}",
        encoders = ChatMessageEncoder.class,
        decoders = ChatMessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class ChatEndpoint extends AuthenticatedEndpoint {
    private static final Map<Integer, Set<Session>> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(ChatEndpoint.class);

    @Inject
    private EntityManagerProducer entityManagerProducer;

    @Inject
    @NewChat
    private Event<ChatEvent> newChatEvent;

    @OnOpen
    public void onOpen(Session currentSession, @PathParam("id") int id){
        Set<Session> chatSessions = sessionsMap.getOrDefault(id, Collections.synchronizedSet(new HashSet<>()));
        chatSessions.add(currentSession);
        sessionsMap.put(id, chatSessions);

        broadcastUserConnected(currentSession, id);
        broadcastAvailableUsers(id);

        logger.info(String.format("Session %s opened", currentSession.getId()));
        logger.info(String.format("%d sessions open on chat %d", chatSessions.size(), id));
    }

    @OnMessage
    public void onMessage(Session currentSession, ChatSocketMessage msg, @PathParam("id") int id) {
        if(msg.getPayload() instanceof SendTextMessagePayload){
            SendTextMessagePayload payload = (SendTextMessagePayload) msg.getPayload();
            final int userID = getUserID(currentSession);
            broadcastTextMessage(userID, payload.getContent(), id);
        }
    }

    @OnClose
    public void onClose(Session currentSession, @PathParam("id") int id){
        Set<Session> chatSessions = sessionsMap.get(id);
        chatSessions.remove(currentSession);

        broadcastUserDisconnected(getUserID(currentSession), id);
        broadcastAvailableUsers(id);

        logger.info(String.format("Session %s closed", currentSession.getId()));
        logger.info(String.format("%d sessions open on chat %d", chatSessions.size(), id));
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        logger.error(throwable.getMessage());
    }

    private void broadcastUserConnected(Session currentSession, int chatID) {
        BasicUserData data = getPrincipal(currentSession).getData();
        BroadcastConnectedUserPayload payload = new BroadcastConnectedUserPayload(data);
        broadcast(currentSession, new ChatSocketMessage(payload), chatID);
    }

    private void broadcastUserDisconnected(int id, int chatID) {
        BroadcastDisconnectedUserPayload payload = new BroadcastDisconnectedUserPayload(id);
        broadcast(new ChatSocketMessage(payload), chatID);
    }

    private void broadcastAvailableUsers(int chatID) {
        Set<Session> chatSessions = sessionsMap.get(chatID);
        Set<BasicUserData> users = chatSessions.stream()
                .map(this::getPrincipal)
                .map(AuthenticatedPrincipal::getData)
                .collect(Collectors.toSet());

        BroadcastAvailableUsersPayload payload = new BroadcastAvailableUsersPayload(users);
        broadcast(new ChatSocketMessage(payload), chatID);
    }

    private void broadcastTextMessage(int userID, String text, int chatID) {
        final LocalDateTime date = LocalDateTime.now();
        int id = sendMessage(chatID, userID, text, date);
        MessageJSON message = new MessageJSON(id, userID, text, date.toString());
        BroadcastTextMessagePayload payload = new BroadcastTextMessagePayload(message);
        broadcast(new ChatSocketMessage(payload), chatID);
    }

    private void broadcast(ChatSocketMessage msg, int chatID){
        synchronized (sessionsMap) {
            Set<Session> chatSessions = sessionsMap.get(chatID);
            chatSessions.stream()
                    .filter(Session::isOpen)
                    .forEach(session -> session.getAsyncRemote().sendObject(msg));

            logger.info(String.format("Broadcasting \"%s\" to %d sessions in chat %d", msg, chatSessions.size(), chatID));
        }
    }

    private void broadcast(Session currentSession, ChatSocketMessage msg, int chatID){
        synchronized (sessionsMap) {
            Set<Session> chatSessions = sessionsMap.get(chatID);
            chatSessions.stream()
                    .filter(Session::isOpen)
                    .filter(session -> !session.getId().equals(currentSession.getId()))
                    .forEach(session -> session.getAsyncRemote().sendObject(msg));

            logger.info(String.format("Broadcasting \"%s\" to %d sessions in chat %d", msg, chatSessions.size(), chatID));
        }
    }

    private int sendMessage(int chatID, int userID, String text, LocalDateTime date){
        EntityManager em = entityManagerProducer.createEntityManager();
        ChatManager chatManager = getChatManager(em);
        int id = chatManager.sendMessage(chatID, userID, text, date);
        final List<Integer> closedChatMembers = chatManager.getClosedChatMembers(chatID);
        logger.info("closedChatMembers: " + closedChatMembers);
        newChatEvent.fire(new ChatEvent(chatID, new HashSet<>(closedChatMembers)));
        closedChatMembers.forEach(uID -> chatManager.openChat(chatID, uID));
        entityManagerProducer.closeEntityManager(em);

        return id;
    }

    private ChatManager getChatManager(EntityManager em){
        UserManager userManager = new UserManager(em, new FriendHelperManager());
        GameManager gameManager = new GameManager(em);
        ActivityManager activityManager = new ActivityManager(em, gameManager);
        ChatPlatformManager chatPlatformManager = new ChatPlatformManager(em);
        GamePlatformManager gamePlatformManager = new GamePlatformManager(em);
        GroupManager groupManager = new GroupManager(em, userManager, activityManager, chatPlatformManager, gamePlatformManager);
        return new ChatManager(em, userManager, groupManager);
    }
}
