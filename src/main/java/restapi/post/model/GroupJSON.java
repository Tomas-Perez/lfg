package restapi.post.model;

import persistence.model.Group;
import restapi.group.model.MemberJSON;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class GroupJSON {
    private int id;
    private List<MemberJSON> members;

    public GroupJSON(int id, List<MemberJSON> members) {
        this.id = id;
        this.members = members;
    }

    public GroupJSON() {
    }

    public GroupJSON(Group group) {
        this.id = group.getId();
        this.members = group.getMembers().stream().map(MemberJSON::new).collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MemberJSON> getMembers() {
        return members;
    }

    public void setMembers(List<MemberJSON> members) {
        this.members = members;
    }
}
