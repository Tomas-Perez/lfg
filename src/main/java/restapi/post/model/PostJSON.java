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
    private ActivityJSON activityJSON;
    private MemberJSON ownerJSON;
    private GroupJSON groupJSON;

    public PostJSON(int id, String description, LocalDateTime date, ActivityJSON activityJSON, MemberJSON ownerJSON, GroupJSON groupJSON) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.activityJSON = activityJSON;
        this.ownerJSON = ownerJSON;
        this.groupJSON = groupJSON;
    }

    public PostJSON() {
    }

    public PostJSON(Post post) {
        this.id = post.getId();
        this.description = post.getDescription();
        this.date = post.getDate();
        this.activityJSON = new ActivityJSON(post.getActivity());
        this.ownerJSON = new MemberJSON(post.getOwner());
        final Group group = post.getGroup();
        this.groupJSON = group == null ? null : new GroupJSON(group);
    }
}
