package api.websocket.post;

import api.common.event.post.*;
import api.websocket.common.config.CdiAwareConfigurator;
import api.websocket.common.model.Payload;
import api.websocket.post.codec.PostMessageDecoder;
import api.websocket.post.codec.PostMessageEncoder;
import api.websocket.post.model.payload.UpdatePostPayload;
import common.postfilter.FilterData;
import api.websocket.post.filter.FilteredPrincipal;
import api.websocket.post.model.PostSocketMessage;
import api.websocket.post.model.payload.DeletePostPayload;
import api.websocket.post.model.payload.NewPostPayload;
import common.postfilter.PostData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.security.Principal;
import java.util.*;

/**
 * @author Tomas Perez Molina
 */

@Dependent
@ServerEndpoint(value = "/websockets/posts",
        encoders = PostMessageEncoder.class,
        decoders = PostMessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class PostEndpoint {
    private static final Map<FilterData, Set<Session>> sessionsMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger logger = LogManager.getLogger(PostEndpoint.class);

    @OnOpen
    public void onOpen(Session currentSession){
        logger.info(String.format("Session %s opened", currentSession.getId()));
        List<FilterData> filters = getFilters(currentSession);
        logger.info(filters);
        filters.forEach(filter -> {
            Set<Session> sessionSet =
                    sessionsMap.getOrDefault(filter, Collections.synchronizedSet(new HashSet<>()));
            sessionSet.add(currentSession);
            sessionsMap.put(filter, sessionSet);
        });
    }

    @OnClose
    public void onClose(Session currentSession){
        List<FilterData> filters = getFilters(currentSession);
        filters.forEach(filter -> {
            Set<Session> sessionSet =
                    sessionsMap.get(filter);
            sessionSet.remove(currentSession);
            sessionsMap.put(filter, sessionSet);
        });
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        logger.error(throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(Session currentSession, PostSocketMessage message) {}

    private List<FilterData> getFilters(Session session){
        Principal principal = session.getUserPrincipal();
        if(principal instanceof FilteredPrincipal){
            return ((FilteredPrincipal) principal).getFilters();
        } else {
            throw new RuntimeException("Principal is not filtered");
        }
    }

    private void newPost(@Observes @NewPost PostEvent event){
        logger.info("firing: " + event);
        NewPostPayload payload = new NewPostPayload(event.getPostID());
        filteredBroadcast(payload, event.getData());
    }

    private void deletePost(@Observes @DeletePost PostEvent event){
        DeletePostPayload payload = new DeletePostPayload(event.getPostID());
        filteredBroadcast(payload, event.getData());
    }

    private void updatePost(@Observes @UpdatePost UpdatePostEvent event){
        UpdatePostPayload payload = new UpdatePostPayload(event.getPostID());
        filteredBroadcast(payload, event.getData());
    }

    private void filteredBroadcast(Payload payload, PostData postData){
        PostSocketMessage msg = new PostSocketMessage(payload);

        synchronized (sessionsMap){
            sessionsMap.entrySet().forEach(logger::info);

            sessionsMap.entrySet().stream()
                    .filter(e -> e.getKey().filter(postData))
                    .map(Map.Entry::getValue)
                    .flatMap(Set::stream)
                    .distinct()
                    .filter(Session::isOpen)
                    .forEach(session -> {
                        logger.info(String.format("Sending %s to session %s", msg, session.getId()));
                        session.getAsyncRemote().sendObject(msg);
                    });
            logger.info("Broadcasting: " + msg);
        }
    }
}
