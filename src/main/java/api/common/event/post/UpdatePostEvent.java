package api.common.event.post;

import common.postfilter.PostData;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class UpdatePostEvent {
    private int postID;
    private PostData data;
    private Set<Integer> notifySet;

    public UpdatePostEvent(int postID, PostData data, Set<Integer> notifySet) {
        this.postID = postID;
        this.data = data;
        this.notifySet = notifySet;
    }

    public int getPostID() {
        return postID;
    }

    public PostData getData() {
        return data;
    }

    public Set<Integer> getNotifySet() {
        return notifySet;
    }

    @Override
    public String toString() {
        return "UpdatePostEvent{" +
                "postID=" + postID +
                ", data=" + data +
                ", notifySet=" + notifySet +
                '}';
    }
}
