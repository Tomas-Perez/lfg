package common.postfilter;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class PostData {
    private Integer gameID;
    private Integer activityID;
    private Set<Integer> chatPlatformIDs;
    private Set<Integer> gamePlatformIDs;
    private FilterData.PostType type;

    private PostData(Integer gameID, Integer activityID, Set<Integer> chatPlatformIDs, Set<Integer> gamePlatformIDs, FilterData.PostType type) {
        this.gameID = gameID;
        this.activityID = activityID;
        this.chatPlatformIDs = chatPlatformIDs;
        this.gamePlatformIDs = gamePlatformIDs;
        this.type = type;
    }

    public Integer getGameID() {
        return gameID;
    }

    public Integer getActivityID() {
        return activityID;
    }

    public Set<Integer> getChatPlatformIDs() {
        return chatPlatformIDs;
    }

    public Set<Integer> getGamePlatformIDs() {
        return gamePlatformIDs;
    }

    public FilterData.PostType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PostData{" +
                "gameID=" + gameID +
                ", activityID=" + activityID +
                ", chatPlatformIDs=" + chatPlatformIDs +
                ", gamePlatformIDs=" + gamePlatformIDs +
                ", type=" + type +
                '}';
    }

    public static class Builder{
        private Integer gameID;
        private Integer activityID;
        private Set<Integer> chatPlatformIDs;
        private Set<Integer> gamePlatformIDs;
        private FilterData.PostType type;

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

        public Builder withChatPlatforms(@NotNull Set<Integer> chatPlatformID){
            this.chatPlatformIDs = chatPlatformID;
            return this;
        }

        public Builder withGamePlatforms(@NotNull Set<Integer> gamePlatformID){
            this.gamePlatformIDs = gamePlatformID;
            return this;
        }

        public Builder withType(@NotNull FilterData.PostType type){
            this.type = type;
            return this;
        }

        public PostData build(){
            return new PostData(gameID, activityID, chatPlatformIDs, gamePlatformIDs, type);
        }
    }
}
