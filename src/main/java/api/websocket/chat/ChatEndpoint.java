package api.websocket.chat;

import api.websocket.chat.codec.ChatMessageDecoder;
import api.websocket.chat.codec.ChatMessageEncoder;
import api.websocket.chat.model.ChatSocketMessage;
import api.websocket.chat.model.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/chat/{id}",
        encoders = ChatMessageEncoder.class,
        decoders = ChatMessageDecoder.class)
public class ChatEndpoint {
    private static final Map<Integer, Set<Session>> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(ChatEndpoint.class);

    @OnOpen
    public void onOpen(Session currentSession, @PathParam("id") int id){
        Set<Session> chatSessions = sessionsMap.getOrDefault(id, Collections.synchronizedSet(new HashSet<>()));
        chatSessions.add(currentSession);
        sessionsMap.put(id, chatSessions);
        final int userID = getUserID(currentSession);
        broadcastUserConnected(currentSession, userID, id);
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

    private void broadcastUserConnected(Session currentSession, int id, int chatID) {
        BroadcastConnectedUserPayload payload = new BroadcastConnectedUserPayload();
        payload.setId(id);
        broadcast(currentSession, new ChatSocketMessage(payload), chatID);
    }

    private void broadcastUserDisconnected(int id, int chatID) {
        BroadcastDisconnectedUserPayload payload = new BroadcastDisconnectedUserPayload();
        payload.setId(id);
        broadcast(new ChatSocketMessage(payload), chatID);
    }

    private void broadcastAvailableUsers(int chatID) {
        Set<Session> chatSessions = sessionsMap.get(chatID);
        Set<Integer> ids = chatSessions.stream()
                .map(Session::getUserPrincipal)
                .map(Principal::getName)
                .map(Integer::parseInt)
                .distinct()
                .collect(Collectors.toSet());

        BroadcastAvailableUsersPayload payload = new BroadcastAvailableUsersPayload();
        payload.setIds(ids);
        broadcast(new ChatSocketMessage(payload), chatID);
    }

    private void broadcastTextMessage(int id, String text, int chatID) {
        BroadcastTextMessagePayload payload = new BroadcastTextMessagePayload();
        payload.setContent(text);
        payload.setId(id);
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

    private int getUserID(Session session){
        return Integer.parseInt(session.getUserPrincipal().getName());
    }
}
