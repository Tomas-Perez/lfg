package persistence.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "crew")
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "slots")
    private int slots;

    @ManyToOne
    @JoinColumn(name="activity_id", referencedColumnName="id")
    private Activity activity;

    @ManyToOne(optional = false)
    @JoinColumn(name="owner_id", referencedColumnName="id")
    private User owner;

    @ManyToOne
    @JoinColumn(name="chat_platform_id", referencedColumnName="id")
    private ChatPlatform chatPlatform;

    @ManyToOne
    @JoinColumn(name="game_platform_id", referencedColumnName="id")
    private GamePlatform gamePlatform;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "group_member",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<User> members;

    public Group(int slots, Activity activity, User owner, ChatPlatform chatPlatform, GamePlatform gamePlatform) {
        this.slots = slots;
        this.activity = activity;
        this.owner = owner;
        this.chatPlatform = chatPlatform;
        this.gamePlatform = gamePlatform;
        this.members = new HashSet<>(this.slots);
        this.members.add(owner);
    }

    public Group() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
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

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ChatPlatform getChatPlatform() {
        return chatPlatform;
    }

    public void setChatPlatform(ChatPlatform chatPlatform) {
        this.chatPlatform = chatPlatform;
    }

    public GamePlatform getGamePlatform() {
        return gamePlatform;
    }

    public void setGamePlatform(GamePlatform gamePlatform) {
        this.gamePlatform = gamePlatform;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(@NotNull User member){
        if(full()) return;
        members.add(member);
    }

    public void removeMember(@NotNull User member){
        members.remove(member);
    }

    public boolean full(){
        return members.size() == slots;
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
}
