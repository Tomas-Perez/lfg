package api.websocket.group.codec;

import api.websocket.group.model.GroupSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author Tomas Perez Molina
 */
public class GroupMessageEncoder implements Encoder.Text<GroupSocketMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

    @Override
    public String encode(GroupSocketMessage message) throws EncodeException {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new EncodeException(message, e.getMessage(), e);
        }
    }
}
