package api.websocket.group.model.payload;

import api.websocket.common.model.Payload;

/**
 *
 * @author Tomas Perez Molina
 */
public class NewOwnerPayload implements Payload {

    public static final String TYPE = "newOwner";

    private int oldOwnerID;
    private int newOwnerID;

    public NewOwnerPayload(int oldOwnerID, int newOwnerID) {
        this.oldOwnerID = oldOwnerID;
        this.newOwnerID = newOwnerID;
    }

    public int getOldOwnerID() {
        return oldOwnerID;
    }

    public void setOldOwnerID(int oldOwnerID) {
        this.oldOwnerID = oldOwnerID;
    }

    public int getNewOwnerID() {
        return newOwnerID;
    }

    public void setNewOwnerID(int newOwnerID) {
        this.newOwnerID = newOwnerID;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "NewOwnerPayload{" +
                "oldOwnerID=" + oldOwnerID +
                ", newOwnerID=" + newOwnerID +
                '}';
    }
}
