package api.websocket.user.codec;

import api.websocket.user.model.ChatSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Tomas Perez Molina
 */
public class ChatMessageEncoder implements Encoder.Text<ChatSocketMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

    @Override
    public String encode(ChatSocketMessage message) throws EncodeException {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }
}
