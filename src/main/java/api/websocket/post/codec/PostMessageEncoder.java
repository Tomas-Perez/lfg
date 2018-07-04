package api.websocket.post.codec;

import api.websocket.post.model.PostSocketMessage;
import api.websocket.user.model.UserSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Tomas Perez Molina
 */
public class PostMessageEncoder implements Encoder.Text<PostSocketMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

    @Override
    public String encode(PostSocketMessage message) throws EncodeException {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }
}
