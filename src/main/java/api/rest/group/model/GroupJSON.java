package api.rest.group.model;

import persistence.model.Group;
import api.rest.activity.model.ActivityJSON;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class GroupJSON {
    private int id;
    private int slots;
    private ActivityJSON activity;
    private MemberJSON owner;
    private List<MemberJSON> members;

    public GroupJSON(int id, int slots, ActivityJSON activity, MemberJSON owner, List<MemberJSON> members) {
        this.id = id;
        this.slots = slots;
        this.activity = activity;
        this.owner = owner;
        this.members = members;
    }

    public GroupJSON() {
    }

    public GroupJSON(Group group) {
        this.id = group.getId();
        this.slots = group.getSlots();
        this.activity = new ActivityJSON(group.getActivity());
        this.owner = new MemberJSON(group.getOwner());
        this.members = group.getMembers().stream().map(MemberJSON::new).collect(Collectors.toList());
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

    public List<MemberJSON> getMembers() {
        return members;
    }

    public void setMembers(List<MemberJSON> members) {
        this.members = members;
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
                new HashSet<>(members).equals(new HashSet<>(groupJSON.members));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slots, activity, owner, members);
    }

    @Override
    public String toString() {
        return "GroupJSON{" +
                "id=" + id +
                ", slots=" + slots +
                ", activity=" + activity +
                ", owner=" + owner +
                ", members=" + members +
                '}';
    }
}
