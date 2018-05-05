package persistence.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "post")
public class Post {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="activity_id", referencedColumnName="id")
    private Activity activity;

    @ManyToOne(optional = false)
    @JoinColumn(name="owner_id", referencedColumnName="id")
    private User owner;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName="id")
    private Group group;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "game_platform_for_post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "game_platform_id"))
    private Set<GamePlatform> gamePlatforms;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "chat_platform_for_post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_platform_id"))
    private Set<ChatPlatform> chatPlatforms;

    public Post(int id, String description, LocalDateTime date, Activity activity, User owner) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.activity = activity;
        this.owner = owner;
        this.gamePlatforms = new HashSet<>();
        this.chatPlatforms = new HashSet<>();
    }

    public Post(int id, String description, LocalDateTime date, Group group) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.group = group;
        this.activity = group.getActivity();
        this.owner = group.getOwner();
        this.gamePlatforms = new HashSet<>();
        this.chatPlatforms = new HashSet<>();
    }

    public Post() {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Set<GamePlatform> getGamePlatforms() {
        return gamePlatforms;
    }

    public void setGamePlatforms(Set<GamePlatform> gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
    }

    public Set<ChatPlatform> getChatPlatforms() {
        return chatPlatforms;
    }

    public void setChatPlatforms(Set<ChatPlatform> chatPlatforms) {
        this.chatPlatforms = chatPlatforms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}