package common.postfilter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private Integer chatPlatformID;
    private Integer gamePlatformID;
    private PostType type;

    public FilterData(Integer gameID, Integer activities) {
        this.gameID = gameID;
        this.activityID = activities;
    }

    public FilterData(Integer gameID, Integer activityID, Integer chatPlatformID, Integer gamePlatformID, PostType type) {
        this.gameID = gameID;
        this.activityID = activityID;
        this.chatPlatformID = chatPlatformID;
        this.gamePlatformID = gamePlatformID;
        this.type = type;
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

    public Integer getChatPlatformID() {
        return chatPlatformID;
    }

    public Integer getGamePlatformID() {
        return gamePlatformID;
    }

    public PostType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FilterData{" +
                "gameID=" + gameID +
                ", activityID=" + activityID +
                '}';
    }

    public String asQueryParam(){
        List<String> params = Arrays.asList(activityCode(), chatPlatfromCode(), gamePlatfromCode(), typeCode());
        String strParams = params.stream()
                .filter(x -> !x.isEmpty())
                .collect(Collectors.joining(","));
        if(strParams.isEmpty()) return "";
        else return "filter=" + strParams;
    }


    private String chatPlatfromCode(){
        return chatPlatformID == null? "" : String.format("%c%d", CHAT_PLATFORM_DELIM, chatPlatformID);
    }

    private String gamePlatfromCode(){
        return gamePlatformID == null? "" : String.format("%c%d", GAME_PLATFORM_DELIM, gamePlatformID);
    }

    private String activityCode(){
        StringBuilder builder = new StringBuilder();
        if(gameID != null){
            builder.append(String.format("%c%d", ACTIVITY_DELIM, gameID));
            if(activityID != null){
                builder.append(String.format(":%d", activityID));
            }
        }
        return builder.toString();
    }

    private String typeCode(){
        return type == null? "" : String.format("%c%s", TYPE_DELIM, type);
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
        LFG("lfg"), LFM("lfm");

        private String name;

        PostType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Builder{
        private Integer gameID;
        private Integer activityID;
        private Integer chatPlatformID;
        private Integer gamePlatformID;
        private PostType type;

        public Builder withGame(int gameID){
            this.gameID = gameID;
            this.activityID = null;
            return this;
        }

        public Builder withActivity(int gameID, int activityID){
            this.gameID = gameID;
            this.activityID = activityID;
            return this;
        }

        public Builder withChatPlatform(int chatPlatformID){
            this.chatPlatformID = chatPlatformID;
            return this;
        }

        public Builder withGamePlatform(int gamePlatformID){
            this.gamePlatformID = gamePlatformID;
            return this;
        }

        public Builder withType(@NotNull PostType type){
            this.type = type;
            return this;
        }

        public FilterData build(){
            return new FilterData(gameID, activityID, chatPlatformID, gamePlatformID, type);
        }
    }
}
