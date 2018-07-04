package api.common.event.post;

/**
 * @author Tomas Perez Molina
 */
public class PostEvent {
    private int postID;
    private Integer gameID;
    private Integer activityID;

    public PostEvent(int postID, Integer gameID, Integer activityID) {
        this.postID = postID;
        this.gameID = gameID;
        this.activityID = activityID;
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
                "postID=" + postID +
                ", gameID=" + gameID +
                ", activityID=" + activityID +
                '}';
    }
}
