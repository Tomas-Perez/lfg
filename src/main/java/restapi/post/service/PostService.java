package restapi.post.service;

import persistence.entity.GroupEntity;
import persistence.manager.GroupManager;
import persistence.manager.PostManager;
import persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class PostService {

    @Inject
    private GroupManager groupManager;

    @Inject
    private PostManager postManager;

    @Inject
    private ModelBuilder modelBuilder;

    public List<Post> getAll(){
        return postManager.listPosts().stream().map(modelBuilder::buildPost).collect(Collectors.toList());
    }

    public int newPost(String description, int activityID, int ownerID){
        return postManager.addPost(description, LocalDateTime.now(), activityID, ownerID);
    }

    public int newGroupPost(String description, int groupID){
        GroupEntity group = getGroup(groupID);
        return postManager.addGroupPost(description, LocalDateTime.now(), group);
    }

    public Post getPost(int id){
        try {
            return modelBuilder.buildPost(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
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


    private GroupEntity getGroup(int groupID){
        GroupEntity group = groupManager.getGroup(groupID);
        if(group == null) throw new NotFoundException("Group not found");
        return group;
    }
}