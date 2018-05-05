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

    @ManyToOne(optional = false)
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
        setActivity(activity);
        this.members = new HashSet<>(this.slots);
        setChatPlatform(chatPlatform);
        setGamePlatform(gamePlatform);
        setOwner(owner);
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
        if(sameAsFormer(activity))
            return;

        Activity oldActivity = this.activity;
        this.activity = activity;

        if(oldActivity != null)
            oldActivity.removeGroup(this);

        if(activity != null)
            activity.addGroup(this);
    }

    private boolean sameAsFormer(Activity newActivity){
        return activity == null ? newActivity == null : activity.equals(newActivity);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        if(sameAsFormer(owner))
            return;

        User oldOwner = this.owner;
        this.owner = owner;

        if(oldOwner != null)
            oldOwner.removeFromGroup(this);

        if(owner != null)
            owner.addToGroup(this);
    }

    private boolean sameAsFormer(User newOwner){
        return owner == null ? newOwner == null : owner.equals(newOwner);
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

        if(members.contains(member))
            return;

        members.add(member);
        member.addToGroup(this);
    }

    public void removeMember(@NotNull User member){
        if(!members.contains(member))
            return;

        members.remove(member);
        member.removeFromGroup(this);
        if(owner.equals(member)) {
            if (!members.isEmpty())
                owner = members.iterator().next();
            else
                owner = null;
        }
    }

    public boolean full(){
        return members.size() == slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
