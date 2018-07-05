package api.common.event.post;

/**
 * @author Tomas Perez Molina
 */
public class PostEvent {
    private int ownerID;
    private int postID;
    private Integer gameID;
    private Integer activityID;

    public PostEvent(int postID, int ownerID, Integer gameID, Integer activityID) {
        this.ownerID = ownerID;
        this.postID = postID;
        this.gameID = gameID;
        this.activityID = activityID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getPostID() {
        return postID;
    }

    public Integer getGameID() {
        return gameID;
    }

    public Integer getActivityID() {
        return activityID;
    }

    @Override
    public String toString() {
        return "PostEvent{" +
                "ownerID=" + ownerID +
                ", postID=" + postID +
                ", gameID=" + gameID +
                ", activityID=" + activityID +
                '}';
    }
}
