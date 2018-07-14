package api.rest.post.model;

import api.rest.chatPlatform.model.ChatPlatformJSON;
import api.rest.gamePlatform.model.GamePlatformJSON;
import persistence.model.Group;
import persistence.model.Post;
import api.rest.activity.model.ActivityJSON;
import api.rest.group.model.MemberJSON;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class PostJSON {
    private int id;
    private String description;
    private String date;
    private ActivityJSON activity;
    private MemberJSON owner;
    private GroupJSON group;
    private Set<ChatPlatformJSON> chatPlatforms;
    private Set<GamePlatformJSON> gamePlatforms;

    public PostJSON(int id,
                    String description,
                    String date,
                    ActivityJSON activity,
                    MemberJSON owner,
                    GroupJSON group,
                    Set<ChatPlatformJSON> chatPlatforms,
                    Set<GamePlatformJSON> gamePlatforms) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.activity = activity;
        this.owner = owner;
        this.group = group;
        this.chatPlatforms = chatPlatforms;
        this.gamePlatforms = gamePlatforms;
    }

    public PostJSON() {
    }

    public PostJSON(Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.date = post.getDate().toString();
        this.activity = new ActivityJSON(post.getActivity());
        this.owner = new MemberJSON(post.getOwner());
        final Group group = post.getGroup();
        this.group = group == null ? null : new GroupJSON(group);
        this.chatPlatforms = post.getChatPlatforms().stream().map(ChatPlatformJSON::new).collect(Collectors.toSet());
        this.gamePlatforms = post.getGamePlatforms().stream().map(GamePlatformJSON::new).collect(Collectors.toSet());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActivityJSON getActivity() {
        return activity;
    }

    public void setActivity(ActivityJSON activity) {
        this.activity = activity;
    }

    public MemberJSON getOwner() {
        return owner;
    }

    public void setOwner(MemberJSON owner) {
        this.owner = owner;
    }

    public GroupJSON getGroup() {
        return group;
    }

    public void setGroup(GroupJSON group) {
        this.group = group;
    }

    public Set<ChatPlatformJSON> getChatPlatforms() {
        return chatPlatforms;
    }

    public void setChatPlatforms(Set<ChatPlatformJSON> chatPlatforms) {
        this.chatPlatforms = chatPlatforms;
    }

    public Set<GamePlatformJSON> getGamePlatforms() {
        return gamePlatforms;
    }

    public void setGamePlatforms(Set<GamePlatformJSON> gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostJSON)) return false;
        PostJSON postJSON = (PostJSON) o;
        return id == postJSON.id &&
                Objects.equals(description, postJSON.description) &&
//                Objects.equals(date, postJSON.date) &&
                Objects.equals(activity, postJSON.activity) &&
                Objects.equals(owner, postJSON.owner) &&
                Objects.equals(group, postJSON.group) &&
                Objects.equals(chatPlatforms, postJSON.chatPlatforms) &&
                Objects.equals(gamePlatforms, postJSON.gamePlatforms);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, description, date, activity, owner, group, chatPlatforms, gamePlatforms);
    }

    @Override
    public String toString() {
        return "PostJSON{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", activity=" + activity +
                ", owner=" + owner +
                ", group=" + group +
                ", chatPlatforms=" + chatPlatforms +
                ", gamePlatforms=" + gamePlatforms +
                '}';
    }
}
