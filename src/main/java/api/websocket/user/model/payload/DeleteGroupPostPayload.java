package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * @author Tomas Perez Molina
 */
public class DeleteGroupPostPayload implements Payload {
    public static final String TYPE = "deleteGroupPost";

    private Integer id;

    public DeleteGroupPostPayload() {}

    public DeleteGroupPostPayload(Integer id) {
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
        return "DeleteGroupPostPayload{" +
                "id=" + id +
                '}';
    }
}
