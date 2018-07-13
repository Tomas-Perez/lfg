package api.common.event.group;

/**
 * @author Tomas Perez Molina
 */
public class NewOwnerEvent {
    private int groupID;
    private int oldOwner;
    private int newOwner;

    public NewOwnerEvent(int groupID, int oldOwner, int newOwner) {
        this.groupID = groupID;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    public int getGroupID() {
        return groupID;
    }

    public int getOldOwner() {
        return oldOwner;
    }

    public int getNewOwner() {
        return newOwner;
    }
}
