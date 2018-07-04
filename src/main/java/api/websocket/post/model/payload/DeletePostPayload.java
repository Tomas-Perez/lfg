package api.websocket.post.model.payload;

import api.websocket.common.model.Payload;

/**
 * @author Tomas Perez Molina
 */
public class DeletePostPayload implements Payload {
    public static final String TYPE = "deletePost";

    private Integer id;

    public DeletePostPayload() {}

    public DeletePostPayload(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "DeletePostPayload{" +
                "id=" + id +
                '}';
    }
}
