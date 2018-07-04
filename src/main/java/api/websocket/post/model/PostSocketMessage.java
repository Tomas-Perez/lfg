package api.websocket.post.model;

import api.websocket.common.model.Payload;
import api.websocket.post.model.payload.DeletePostPayload;
import api.websocket.post.model.payload.NewPostPayload;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Tomas Perez Molina
 */
public class PostSocketMessage {
    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = NewPostPayload.class, name = NewPostPayload.TYPE),
            @JsonSubTypes.Type(value = DeletePostPayload.class, name = DeletePostPayload.TYPE)
    })
    private Payload payload;

    public PostSocketMessage() {}

    public PostSocketMessage(Payload payload) {
        this.type = payload.getType();
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "PostSocketMessage{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
