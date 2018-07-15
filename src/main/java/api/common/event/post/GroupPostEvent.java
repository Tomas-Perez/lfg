package api.common.event.post;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class GroupPostEvent {
    private int postID;
    private Set<Integer> notifySet;

    public GroupPostEvent(int postID, Set<Integer> notifySet) {
        this.postID = postID;
        this.notifySet = notifySet;
    }

    public int getPostID() {
        return postID;
    }

    public Set<Integer> getNotifySet() {
        return notifySet;
    }
}