package common.postfilter;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class FilterData {
    public static final char ACTIVITY_DELIM = 'A';
    public static final char GAME_PLATFORM_DELIM = 'G';
    public static final char CHAT_PLATFORM_DELIM = 'C';
    public static final char TYPE_DELIM = 'T';

    private Integer gameID;
    private Integer activityID;

    public FilterData(Integer gameID, Integer activities) {
        this.gameID = gameID;
        this.activityID = activities;
    }

    public FilterData(Integer gameID) {
        this.gameID = gameID;
    }

    public static FilterData emptyPair() {
        return new FilterData();
    }

    private FilterData() {
    }

    public boolean filter(Integer gameID, Integer activityID){
        if(this.gameID == null) return true;
        if(this.gameID.equals(gameID)){
            if(this.activityID == null) return true;
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
        return "FilterData{" +
                "gameID=" + gameID +
                ", activityID=" + activityID +
                '}';
    }

    public String asQueryParam(){
        if(gameID != null){
            if(activityID != null){
                return String.format("filter=%d:%d", gameID, activityID);
            }
            return String.format("filter=%d", gameID);
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterData)) return false;
        FilterData that = (FilterData) o;
        return Objects.equals(gameID, that.gameID) &&
                Objects.equals(activityID, that.activityID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, activityID);
    }

    public enum PostType{
        LFG, LFM
    }

    public static class Builder{
        private Integer gameID;
        private Integer activityID;
        private Integer chatPlatformID;
        private Integer gamePlatformID;
        private PostType type;
    }
}
