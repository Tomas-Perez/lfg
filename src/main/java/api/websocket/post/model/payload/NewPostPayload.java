package api.websocket.post.model.payload;

import api.websocket.common.model.Payload;

/**
 * @author Tomas Perez Molina
 */
public class NewPostPayload implements Payload {
    public static final String TYPE = "newPost";

    private Integer id;

    public NewPostPayload() {}

    public NewPostPayload(Integer id) {
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
        return "NewPostPayload{" +
                "id=" + id +
                '}';
    }
}
