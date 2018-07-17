package api.common.event.post;

import common.postfilter.PostData;

/**
 * @author Tomas Perez Molina
 */
public class PostEvent {
    private int ownerID;
    private int postID;
    private PostData data;

    public PostEvent(int ownerID, int postID, PostData data) {
        this.ownerID = ownerID;
        this.postID = postID;
        this.data = data;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getPostID() {
        return postID;
    }

    public PostData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PostEvent{" +
                "ownerID=" + ownerID +
                ", postID=" + postID +
                ", data=" + data +
                '}';
    }
}
