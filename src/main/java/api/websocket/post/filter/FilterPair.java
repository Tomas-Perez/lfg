package api.websocket.post.filter;

import java.util.List;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class FilterPair {
    private Integer gameID;
    private List<Integer> activities;

    public FilterPair(Integer gameID, List<Integer> activities) {
        this.gameID = gameID;
        this.activities = activities;
    }

    public FilterPair(Integer gameID) {
        this.gameID = gameID;
    }

    public static FilterPair emptyPair() {
        return new FilterPair();
    }

    private FilterPair() {
    }

    public boolean filter(Integer gameID, Integer activityID){
        if(this.gameID == null) return true;
        if(this.gameID.equals(gameID)){
            if(activityID == null) return true;
            return activities.contains(activityID);
        }
        return false;
    }

    @Override
    public String toString() {
        return "FilterPair{" +
                "gameID=" + gameID +
                ", activities=" + activities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterPair)) return false;
        FilterPair that = (FilterPair) o;
        return Objects.equals(gameID, that.gameID) &&
                Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, activities);
    }
}
