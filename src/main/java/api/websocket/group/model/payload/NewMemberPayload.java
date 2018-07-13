package api.websocket.group.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

/**
 *
 * @author Tomas Perez Molina
 */
public class NewMemberPayload implements Payload {

    public static final String TYPE = "newMember";

    private Integer id;
    private String username;

    public NewMemberPayload(BasicUserData data) {
        this.id = data.getId();
        this.username = data.getUsername();
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
