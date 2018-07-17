package common.postfilter;

import api.websocket.post.filter.exception.MalformedParameterException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    private static final FilterData EMPTY = new FilterData();

    public FilterData(Integer gameID, Integer activities) {
        this.gameID = gameID;
        this.activityID = activities;
    }

    private FilterData(Integer gameID, Integer activityID, Integer chatPlatformID, Integer gamePlatformID, PostType type) {
        this.gameID = gameID;
        this.activityID = activityID;
        this.chatPlatformID = chatPlatformID;
        this.gamePlatformID = gamePlatformID;
        this.type = type;
    }

    public static FilterData emptyPair() {
        return EMPTY;
    }

    private FilterData() {
    }

    public boolean filter(PostData postData){
        return filterActivity(postData.getGameID(), postData.getActivityID()) &&
                filterChatPlatform(postData.getChatPlatformIDs()) &&
                filterGamePlatform(postData.getGamePlatformIDs()) &&
                filterType(postData.getType());
    }

    private boolean filterActivity(Integer gameID, Integer activityID){
        if(this.gameID == null) return true;
        if(this.gameID.equals(gameID)){
            if(this.activityID == null) return true;
            return this.activityID.equals(activityID);
        }
        return false;
    }

    private boolean filterChatPlatform(Set<Integer> chatPlatformIDs){
        if(this.chatPlatformID == null) return true;
        return chatPlatformIDs.contains(this.chatPlatformID);
    }

    private boolean filterGamePlatform(Set<Integer> gamePlatformIDs){
        if(this.gamePlatformID == null) return true;
        return gamePlatformIDs.contains(this.gamePlatformID);
    }

    private boolean filterType(PostType type){
        if(this.type == null) return true;
        return this.type.equals(type);
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


    public String asQueryParam(){
        List<String> params = Arrays.asList(activityCode(), chatPlatformCode(), gamePlatformCode(), typeCode());
        String strParams = params.stream()
                .filter(x -> !x.isEmpty())
                .collect(Collectors.joining(","));
        if(strParams.isEmpty()) return "";
        else return "filter=" + strParams;
    }



    private String chatPlatformCode(){
        return chatPlatformID == null? "" : String.format("%c%d", CHAT_PLATFORM_DELIM, chatPlatformID);
    }

    private String gamePlatformCode(){
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
                Objects.equals(activityID, that.activityID) &&
                Objects.equals(chatPlatformID, that.chatPlatformID) &&
                Objects.equals(gamePlatformID, that.gamePlatformID) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gameID, activityID, chatPlatformID, gamePlatformID, type);
    }

    @Override
    public String toString() {
        return "FilterData{" +
                "gameID=" + gameID +
                ", activityID=" + activityID +
                ", chatPlatformID=" + chatPlatformID +
                ", gamePlatformID=" + gamePlatformID +
                ", type=" + type +
                '}';
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

        public static PostType fromString(String str){
            if(str.equals(LFG.name)) return LFG;
            else if(str.equals(LFM.name)) return LFM;
            return null;
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

        public Builder withActivityFromString(@NotNull String str){
            String[] separatedParam = Arrays.stream(str.split(":"))
                    .filter(x -> !x.isEmpty())
                    .toArray(String[]::new);

            switch (separatedParam.length){
                case 0:
                    return this;
                case 1:
                    this.gameID = Integer.parseInt(separatedParam[0]);
                    return this;
                case 2:
                    this.gameID = Integer.parseInt(separatedParam[0]);
                    this.activityID = Integer.parseInt(separatedParam[1]);
                    return this;
                default:
                    throw new MalformedParameterException("Filter parameter is malformed");
            }
        }

        public FilterData build(){
            return new FilterData(gameID, activityID, chatPlatformID, gamePlatformID, type);
        }
    }
}
