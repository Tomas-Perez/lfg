package persistence.model;

import persistence.entity.PostEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

public class Post {

    private int id;
    private String description;
    private LocalDateTime date;
    private Activity activity;
    private User owner;
    private Group group;
    private Set<GamePlatform> gamePlatforms;
    private Set<ChatPlatform> chatPlatforms;

    public Post(PostEntity entity, Activity activity, User owner, Group group, Set<ChatPlatform> chatPlatforms, Set<GamePlatform> gamePlatforms) {
        this.id = entity.getId();
        this.description = entity.getDescription();
        this.date = entity.getDate();
        this.activity = activity;
        this.owner = owner;
        this.group = group;
        this.gamePlatforms = gamePlatforms;
        this.chatPlatforms = chatPlatforms;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Activity getActivity() {
        return activity;
    }

    public User getOwner() {
        return owner;
    }

    public Group getGroup() {
        return group;
    }

    public Set<GamePlatform> getGamePlatforms() {
        return gamePlatforms;
    }

    public Set<ChatPlatform> getChatPlatforms() {
        return chatPlatforms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id &&
                Objects.equals(description, post.description) &&
                Objects.equals(date, post.date) &&
                Objects.equals(activity, post.activity) &&
                Objects.equals(owner, post.owner) &&
                Objects.equals(group, post.group) &&
                Objects.equals(gamePlatforms, post.gamePlatforms) &&
                Objects.equals(chatPlatforms, post.chatPlatforms);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, description, date, activity, owner, group, gamePlatforms, chatPlatforms);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", activity=" + activity +
                ", owner=" + owner +
                ", group=" + group +
                ", gamePlatforms=" + gamePlatforms +
                ", chatPlatforms=" + chatPlatforms +
                '}';
    }
}