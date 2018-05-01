package restapi.post.service;

import persistence.manager.ActivityManager;
import persistence.manager.GroupManager;
import persistence.manager.PostManager;
import persistence.manager.UserManager;
import persistence.model.Activity;
import persistence.model.Group;
import persistence.model.Post;
import persistence.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class PostService {

    @Inject
    private GroupManager groupManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private UserManager userManager;

    @Inject
    private PostManager postManager;

    public List<Post> getAll(){
        return postManager.listPosts();
    }

    public int newPost(String description, int activityID, int ownerID){
        final Activity activity = getActivity(activityID);
        final User owner = getUser(ownerID);
        return postManager.addPost(description, LocalDateTime.now(), activity, owner);
    }

    public int newGroupPost(String description, int groupID){
        Group group = getGroup(groupID);
        return postManager.addGroupPost(description, LocalDateTime.now(), group);
    }

    public Post getPost(int id){
        final Post post = postManager.getPost(id);
        if(post == null) throw new NotFoundException();
        return post;
    }

    public void wipe(){
        postManager.wipeAllRecords();
    }

    public void deletePost(int id){
        try {
            postManager.deletePost(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }


    private User getUser(int userID){
        User user = userManager.getUser(userID);
        if(user == null) throw new NotFoundException("User not found");
        return user;
    }

    private Activity getActivity(int activityID){
        Activity activity = activityManager.getActivity(activityID);
        if(activity == null) throw new NotFoundException("Activity not found");
        return activity;
    }

    private Group getGroup(int groupID){
        Group group = groupManager.getGroup(groupID);
        if(group == null) throw new NotFoundException("Group not found");
        return group;
    }
}