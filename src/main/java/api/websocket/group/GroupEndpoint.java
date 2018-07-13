package api.websocket.group;

import api.common.event.group.*;
import api.websocket.common.AuthenticatedEndpoint;
import api.websocket.common.config.CdiAwareConfigurator;
import api.websocket.common.model.Payload;
import api.websocket.group.codec.GroupMessageDecoder;
import api.websocket.group.codec.GroupMessageEncoder;
import api.websocket.group.model.GroupSocketMessage;
import api.websocket.group.model.payload.DeleteMemberPayload;
import api.websocket.group.model.payload.NewMemberPayload;
import api.websocket.group.model.payload.NewOwnerPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/groups/{id}",
        encoders = GroupMessageEncoder.class,
        decoders = GroupMessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class GroupEndpoint extends AuthenticatedEndpoint {
    private static final Map<Integer, Set<Session>> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(GroupEndpoint.class);

    @OnOpen
    public void onOpen(Session currentSession, @PathParam("id") int id){
        Set<Session> groupSessions = sessionsMap.getOrDefault(id, Collections.synchronizedSet(new HashSet<>()));
        groupSessions.add(currentSession);
        sessionsMap.put(id, groupSessions);

        logger.info(String.format("Session %s opened", currentSession.getId()));
        logger.info(String.format("%d sessions open on group %d", groupSessions.size(), id));
    }

    @OnClose
    public void onClose(Session currentSession, @PathParam("id") int id){
        Set<Session> chatSessions = sessionsMap.get(id);
        chatSessions.remove(currentSession);

        logger.info(String.format("Session %s closed", currentSession.getId()));
        logger.info(String.format("%d sessions open on group %d", chatSessions.size(), id));
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        logger.error(throwable.getMessage());
    }

    private void broadcast(Payload payload, int chatID){
        GroupSocketMessage msg = new GroupSocketMessage(payload);

        synchronized (sessionsMap) {
            Set<Session> chatSessions = sessionsMap.get(chatID);
            chatSessions.stream()
                    .filter(Session::isOpen)
                    .forEach(session -> session.getAsyncRemote().sendObject(msg));

            logger.info(String.format("Broadcasting \"%s\" to %d sessions in group %d", msg, chatSessions.size(), chatID));
        }
    }

    private void newMember(@Observes @NewMember MemberEvent event){
        Payload newMemberPayload = new NewMemberPayload(event.getMember());
        broadcast(newMemberPayload, event.getGroupID());
    }

    private void deleteMember(@Observes @DeleteMember MemberEvent event){
        Payload newMemberPayload = new DeleteMemberPayload(event.getMember().getId());
        broadcast(newMemberPayload, event.getGroupID());
    }

    private void newOwner(@Observes @NewOwner NewOwnerEvent event){
        Payload newMemberPayload = new NewOwnerPayload(event.getOldOwner(), event.getNewOwner());
        broadcast(newMemberPayload, event.getGroupID());
    }
}
