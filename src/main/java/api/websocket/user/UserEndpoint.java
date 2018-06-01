package api.websocket.user;

import api.websocket.user.codec.ChatMessageDecoder;
import api.websocket.user.codec.ChatMessageEncoder;
import api.websocket.user.model.ChatSocketMessage;
import api.websocket.user.model.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
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
@ServerEndpoint(value = "/websockets/users",
        encoders = ChatMessageEncoder.class,
        decoders = ChatMessageDecoder.class)
public class UserEndpoint {
    private static final Map<Integer, Session> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Map<Integer, Session> recentlyConnectedMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(UserEndpoint.class);

    @OnOpen
    public void onOpen(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.put(userID, currentSession);
        if(recentlyConnectedMap.get(userID) == null)
            broadcastUserConnected(userID);

        logger.info(String.format("Session %s opened", currentSession.getId()));
    }

    @OnMessage
    public void onMessage(Session currentSession, ChatSocketMessage message) {}

    @OnClose
    public void onClose(Session currentSession){
        final int userID = getUserID(currentSession);
        sessionsMap.remove(userID);
        recentlyConnectedMap.put(userID, currentSession);

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(sessionsMap.get(userID) == null) {
                            recentlyConnectedMap.remove(userID);
                            broadcastUserDisconnected(userID);
                        }
                    }
                },
                3000
        );

        logger.info(String.format("Session %s closed", currentSession.getId()));
    }

    private void broadcastUserConnected(int id) {
        BroadcastConnectedUserPayload payload = new BroadcastConnectedUserPayload();
        payload.setId(id);
        broadcast(new ChatSocketMessage(payload), id);
        logger.info(String.format("User %d connected", id));
    }

    private void broadcastUserDisconnected(int id) {
        BroadcastDisconnectedUserPayload payload = new BroadcastDisconnectedUserPayload();
        payload.setId(id);
        broadcast(new ChatSocketMessage(payload), id);
        logger.info(String.format("User %d disconnected", id));
    }

    private void broadcast(ChatSocketMessage msg, int userID){
        synchronized (sessionsMap) {
            sessionsMap.forEach((id, session) -> {
                if(id != userID && session.isOpen()){
                    session.getAsyncRemote().sendObject(msg);
                }
            });
            logger.info(String.format("Broadcasting \"%s\" to all sessions except session of user %d", msg, userID));
        }
    }

    private int getUserID(Session session){
        return Integer.parseInt(session.getUserPrincipal().getName());
    }
}
