package api.common.event.group;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class GroupEvent {
    private int groupID;
    private Set<Integer> notifySet;

    public GroupEvent(int id, Set<Integer> notifyList) {
        this.groupID = id;
        this.notifySet = notifyList;
    }

    public int getId() {
        return groupID;
    }

    public Set<Integer> getNotifySet() {
        return notifySet;
    }
}
