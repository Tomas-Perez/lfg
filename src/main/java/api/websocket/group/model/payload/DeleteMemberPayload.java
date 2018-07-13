package api.websocket.group.model.payload;

import api.websocket.common.model.Payload;

/**
 *
 * @author Tomas Perez Molina
 */
public class DeleteMemberPayload implements Payload {

    public static final String TYPE = "deleteMember";

    private int id;

    public DeleteMemberPayload(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "DeleteMemberPayload{" +
                "id='" + id + '\'' +
                '}';
    }
}
