package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * @author Tomas Perez Molina
 */
public class NewGroupPayload implements Payload {
    public static final String TYPE = "newGroup";

    private Integer id;

    public NewGroupPayload() {}

    public NewGroupPayload(Integer id) {
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
        return "NewGroupPayload{" +
                "id=" + id +
                '}';
    }
}
