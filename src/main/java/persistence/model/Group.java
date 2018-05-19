package persistence.model;

import org.jetbrains.annotations.NotNull;
import persistence.entity.GroupEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

public class Group {

    private int id;
    private int slots;
    private Activity activity;
    private User owner;
    private ChatPlatform chatPlatform;
    private GamePlatform gamePlatform;
    private Set<User> members;

    public Group(GroupEntity entity, Activity activity, User owner, ChatPlatform chatPlatform, GamePlatform gamePlatform, Set<User> members) {
        this.id = entity.getId();
        this.slots = entity.getSlots();
        this.activity = activity;
        this.owner = owner;
        this.chatPlatform = chatPlatform;
        this.gamePlatform = gamePlatform;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public int getSlots() {
        return slots;
    }

    public Activity getActivity() {
        return activity;
    }

    public User getOwner() {
        return owner;
    }

    public ChatPlatform getChatPlatform() {
        return chatPlatform;
    }

    public GamePlatform getGamePlatform() {
        return gamePlatform;
    }

    public Set<User> getMembers() {
        return members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id &&
                slots == group.slots &&
                Objects.equals(activity, group.activity) &&
                Objects.equals(owner, group.owner) &&
                Objects.equals(chatPlatform, group.chatPlatform) &&
                Objects.equals(gamePlatform, group.gamePlatform) &&
                Objects.equals(members, group.members);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slots, activity, owner, chatPlatform, gamePlatform, members);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", slots=" + slots +
                ", activity=" + activity +
                ", owner=" + owner +
                ", chatPlatform=" + chatPlatform +
                ", gamePlatform=" + gamePlatform +
                ", members=" + members +
                '}';
    }
}
