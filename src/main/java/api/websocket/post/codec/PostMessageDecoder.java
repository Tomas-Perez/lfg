package api.websocket.post.codec;

import api.websocket.post.model.PostSocketMessage;
import api.websocket.user.model.UserSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Tomas Perez Molina
 */
public class PostMessageDecoder implements Decoder.Text<PostSocketMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

    @Override
    public PostSocketMessage decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, PostSocketMessage.class);
        } catch (IOException e) {
            throw new DecodeException(s, e.getMessage(), e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }
}
