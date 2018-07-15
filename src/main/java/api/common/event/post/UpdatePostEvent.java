package api.common.event.post;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class UpdatePostEvent {
    private int postID;
    private Integer gameID;
    private Integer activityID;
    private Set<Integer> notifySet;

    public UpdatePostEvent(int postID, Integer gameID, Integer activityID, Set<Integer> notifySet) {
        this.postID = postID;
        this.gameID = gameID;
        this.activityID = activityID;
        this.notifySet = notifySet;
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

    public Set<Integer> getNotifySet() {
        return notifySet;
    }

    @Override
    public String toString() {
        return "UpdatePostEvent{" +
                "postID=" + postID +
                ", gameID=" + gameID +
                ", activityID=" + activityID +
                ", notifySet=" + notifySet +
                '}';
    }
}
