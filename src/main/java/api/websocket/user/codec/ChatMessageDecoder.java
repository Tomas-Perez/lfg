package api.websocket.user.codec;

import api.websocket.user.model.ChatSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

/**
 * @author Tomas Perez Molina
 */
public class ChatMessageDecoder implements Decoder.Text<ChatSocketMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig ec) {}

    @Override
    public void destroy() {}

    @Override
    public ChatSocketMessage decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, ChatSocketMessage.class);
        } catch (IOException e) {
            throw new DecodeException(s, e.getMessage(), e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }
}
