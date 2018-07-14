package api.rest.group.model;

import api.rest.chatPlatform.model.ChatPlatformJSON;
import api.rest.gamePlatform.model.GamePlatformJSON;
import api.rest.user.model.ChatJSON;
import persistence.model.ChatPlatform;
import persistence.model.GamePlatform;
import persistence.model.Group;
import api.rest.activity.model.ActivityJSON;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class GroupJSON {
    private int id;
    private int slots;
    private ActivityJSON activity;
    private MemberJSON owner;
    private Set<MemberJSON> members;
    private ChatJSON chat;
    private GamePlatformJSON gamePlatform;
    private ChatPlatformJSON chatPlatform;

    public GroupJSON(int id,
                     int slots,
                     ActivityJSON activity,
                     MemberJSON owner,
                     Set<MemberJSON> members,
                     ChatJSON chat,
                     GamePlatformJSON gamePlatform,
                     ChatPlatformJSON chatPlatform) {
        this.id = id;
        this.slots = slots;
        this.activity = activity;
        this.owner = owner;
        this.members = members;
        this.chat = chat;
        this.gamePlatform = gamePlatform;
        this.chatPlatform = chatPlatform;
    }

    public GroupJSON() {
    }

    public GroupJSON(Group group) {
        this.id = group.getId();
        this.slots = group.getSlots();
        this.activity = new ActivityJSON(group.getActivity());
        this.owner = new MemberJSON(group.getOwner());
        this.members = group.getMembers().stream().map(MemberJSON::new).collect(Collectors.toSet());
        this.chat = new ChatJSON(group.getChat());
        final GamePlatform gamePlatform = group.getGamePlatform();
        this.gamePlatform = gamePlatform == null? null : new GamePlatformJSON(gamePlatform);
        final ChatPlatform chatPlatform = group.getChatPlatform();
        this.chatPlatform = chatPlatform == null? null : new ChatPlatformJSON(chatPlatform);
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

    public Set<MemberJSON> getMembers() {
        return members;
    }

    public void setMembers(Set<MemberJSON> members) {
        this.members = members;
    }

    public ChatJSON getChat() {
        return chat;
    }

    public void setChat(ChatJSON chat) {
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupJSON)) return false;
        GroupJSON groupJSON = (GroupJSON) o;
        return id == groupJSON.id &&
                slots == groupJSON.slots &&
                Objects.equals(activity, groupJSON.activity) &&
                Objects.equals(owner, groupJSON.owner) &&
                Objects.equals(members, groupJSON.members) &&
//                Objects.equals(chat, groupJSON.chat) &&
                Objects.equals(gamePlatform, groupJSON.gamePlatform) &&
                Objects.equals(chatPlatform, groupJSON.chatPlatform);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slots, activity, owner, members, gamePlatform, chatPlatform);
    }

    @Override
    public String toString() {
        return "GroupJSON{" +
                "id=" + id +
                ", slots=" + slots +
                ", activity=" + activity +
                ", owner=" + owner +
                ", members=" + members +
                ", chat=" + chat +
                ", gamePlatform=" + gamePlatform +
                ", chatPlatform=" + chatPlatform +
                '}';
    }
}
