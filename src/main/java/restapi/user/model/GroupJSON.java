package restapi.user.model;

import persistence.model.Group;
import restapi.activity.model.ActivityJSON;
import restapi.group.model.MemberJSON;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GroupJSON {
    private int id;
    private int slots;
    private ActivityJSON activityJSON;
    private MemberJSON ownerJSON;

    public GroupJSON(int id, int slots, ActivityJSON activityJSON, MemberJSON ownerJSON) {
        this.id = id;
        this.slots = slots;
        this.activityJSON = activityJSON;
        this.ownerJSON = ownerJSON;
    }

    public GroupJSON(Group group) {
        this.id = group.getId();
        this.slots = group.getSlots();
        this.activityJSON = new ActivityJSON(group.getActivity());
        this.ownerJSON = new MemberJSON(group.getOwner());
    }

    public GroupJSON() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActivityJSON getActivityJSON() {
        return activityJSON;
    }

    public void setActivityJSON(ActivityJSON activityJSON) {
        this.activityJSON = activityJSON;
    }

    public MemberJSON getOwnerJSON() {
        return ownerJSON;
    }

    public void setOwnerJSON(MemberJSON ownerJSON) {
        this.ownerJSON = ownerJSON;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupJSON)) return false;
        GroupJSON groupJSON = (GroupJSON) o;
        return id == groupJSON.id &&
                slots == groupJSON.slots &&
                Objects.equals(activityJSON, groupJSON.activityJSON) &&
                Objects.equals(ownerJSON, groupJSON.ownerJSON);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slots, activityJSON, ownerJSON);
    }

    @Override
    public String toString() {
        return "GroupJSON{" +
                "id=" + id +
                ", slots=" + slots +
                ", activityJSON=" + activityJSON +
                ", ownerJSON=" + ownerJSON +
                '}';
    }
}
