package api.websocket.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface that must be implemented by classes that represent the payload of a WebSocketMessage.
 *
 * @author Tomas Perez Molina
 */

public interface Payload {
    @JsonIgnore
    String getType();
}
