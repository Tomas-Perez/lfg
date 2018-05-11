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

    public GroupJSON(int id, int slots, ActivityJSON activity, MemberJSON owner) {
        this.id = id;
    }

    public GroupJSON(Group group) {
        this.id = group.getId();
    }

    public GroupJSON() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupJSON)) return false;
        GroupJSON groupJSON = (GroupJSON) o;
        return id == groupJSON.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GroupJSON{" +
                "id=" + id + '}';
    }
}
