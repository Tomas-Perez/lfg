package restapi.post.model;

import persistence.model.Group;
import persistence.model.Post;
import restapi.activity.model.ActivityJSON;
import restapi.group.model.MemberJSON;
import restapi.group.model.GroupJSON;

import java.time.LocalDateTime;

/**
 * @author Tomas Perez Molina
 */
public class PostJSON {
    private int id;
    private String description;
    private LocalDateTime date;
    private ActivityJSON activity;
    private MemberJSON owner;
    private GroupJSON group;

    public PostJSON(int id, String description, LocalDateTime date, ActivityJSON activity, MemberJSON owner, GroupJSON group) {
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
        this.date = post.getDate();
        this.activity = new ActivityJSON(post.getActivity());
        this.owner = new MemberJSON(post.getOwner());
        final Group group = post.getGroup();
        this.group = group == null ? null : new GroupJSON(group);
    }
}
