package common.postfilter;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class FilterPair {
    private Integer gameID;
    private Integer activityID;

    public FilterPair(Integer gameID, Integer activities) {
        this.gameID = gameID;
        this.activityID = activities;
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
            return this.activityID.equals(activityID);
        }
        return false;
    }

    public Integer getGameID() {
        return gameID;
    }

    public Integer getActivityID() {
        return activityID;
    }

    @Override
    public String toString() {
        return "FilterPair{" +
                "gameID=" + gameID +
                ", activityID=" + activityID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterPair)) return false;
        FilterPair that = (FilterPair) o;
        return Objects.equals(gameID, that.gameID) &&
                Objects.equals(activityID, that.activityID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, activityID);
    }
}
