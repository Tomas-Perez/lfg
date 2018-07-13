package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * @author Tomas Perez Molina
 */
public class DeleteGroupPayload implements Payload {
    public static final String TYPE = "deleteGroup";

    private Integer id;

    public DeleteGroupPayload() {}

    public DeleteGroupPayload(Integer id) {
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
        return "DeleteGroupPayload{" +
                "id=" + id +
                '}';
    }
}
