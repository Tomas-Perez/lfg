package restapi.post.model;

import persistence.model.Group;
import persistence.model.Post;
import restapi.activity.model.ActivityJSON;
import restapi.group.model.MemberJSON;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class PostJSON {
    private int id;
    private String description;
    private String date;
    private ActivityJSON activity;
    private MemberJSON owner;
    private GroupJSON group;

    public PostJSON(int id, String description, String date, ActivityJSON activity, MemberJSON owner, GroupJSON group) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.activity = activity;
        this.owner = owner;
        this.group = group;
    }

    public PostJSON() {
    }

    public PostJSON(Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.date = post.getDate().toString();
        this.activity = new ActivityJSON(post.getActivity());
        this.owner = new MemberJSON(post.getOwner());
        final Group group = post.getGroup();
        this.group = group == null ? null : new GroupJSON(group);
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public GroupJSON getGroup() {
        return group;
    }

    public void setGroup(GroupJSON group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostJSON)) return false;
        PostJSON postJSON = (PostJSON) o;
        return id == postJSON.id &&
                Objects.equals(description, postJSON.description) &&
                Objects.equals(activity, postJSON.activity) &&
                Objects.equals(owner, postJSON.owner) &&
                Objects.equals(group, postJSON.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, activity, owner, group);
    }

    @Override
    public String toString() {
        return "PostJSON{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", activity=" + activity +
                ", owner=" + owner +
                ", group=" + group +
                '}';
    }
}
