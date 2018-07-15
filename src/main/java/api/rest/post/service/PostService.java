package api.rest.post.service;

import api.common.event.post.*;
import common.postfilter.FilterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.entity.GroupEntity;
import persistence.entity.PostEntity;
import persistence.manager.GroupManager;
import persistence.manager.PostManager;
import persistence.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class PostService {
    private static final Logger logger = LogManager.getLogger(PostService.class);


    @Inject
    private GroupManager groupManager;

    @Inject
    private PostManager postManager;

    @Inject
    private ModelBuilder modelBuilder;

    @Inject
    @NewPost
    private Event<PostEvent> newPostEvent;

    @Inject
    @NewGroupPost
    private Event<GroupPostEvent> newGroupPostEvent;

    @Inject
    @DeletePost
    private Event<PostEvent> deletePostEvent;

    @Inject
    @DeleteGroupPost
    private Event<GroupPostEvent> deleteGroupPostEvent;

    @Inject
    @UpdatePost
    private Event<UpdatePostEvent> updatePostEvent;

    public List<Post> getAll(){
        return postManager.list().stream().map(modelBuilder::buildPost).collect(Collectors.toList());
    }

    public List<Post> getFilteredPosts(List<FilterData> filters){
        return filters.stream()
                .map(filter -> postManager.filteredList(filter))
                .flatMap(List::stream)
                .distinct()
                .map(modelBuilder::buildPost)
                .sorted(Comparator.comparing(Post::getDate).reversed()).collect(Collectors.toList());
    }

    public int newPost(String description,
                       int activityID,
                       int ownerID,
                       Set<Integer> chatPlatformIDs,
                       Set<Integer> gamePlatformIDs){
        Integer previousPostID = postManager.getUserPost(ownerID);
        if(previousPostID != null) {
            deletePost(previousPostID);
        }
        PostEntity postEntity = new PostEntity(description, LocalDateTime.now(), activityID, ownerID, null);
        final int id = postManager.add(postEntity, chatPlatformIDs, gamePlatformIDs);
        newPostEvent.fire(createEvent(id));
        return id;
    }

    public int newGroupPost(String description,
                            int groupID,
                            Set<Integer> chatPlatformIDs,
                            Set<Integer> gamePlatformIDs){
        GroupEntity group = getGroup(groupID);
        Integer previousPostID = postManager.getUserPost(group.getOwnerId());
        if(previousPostID != null) {
            deletePost(previousPostID);
        }
        final int id = postManager.addGroupPost(description, LocalDateTime.now(), group, chatPlatformIDs, gamePlatformIDs);
        newPostEvent.fire(createEvent(id));
        newGroupPostEvent.fire(createGroupEvent(id));
        return id;
    }

    public Post getPost(int id){
        try {
            return modelBuilder.buildPost(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void wipe(){
        postManager.wipe();
    }

    public void deletePost(int id){
        try {
            Integer groupID = postManager.getPostGroup(id);
            if(groupID != null) deleteGroupPost(id);
            else deleteLonePost(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    private void deleteGroupPost(int id){
        GroupPostEvent event = createGroupEvent(id);
        deleteLonePost(id);
        deleteGroupPostEvent.fire(event);
    }

    private void deleteLonePost(int id){
        PostEvent event = createEvent(id);
        postManager.delete(id);
        deletePostEvent.fire(event);
    }

    private GroupEntity getGroup(int groupID){
        GroupEntity group = groupManager.get(groupID);
        if(group == null) throw new NotFoundException("Group not found");
        return group;
    }

    private PostEvent createEvent(int id) {
        Post post = modelBuilder.buildPost(id);
        final Activity activity = post.getActivity();
        final Game game = activity.getGame();
        final User owner = post.getOwner();
        return new PostEvent(owner.getId(), post.getId(), game.getId(), activity.getId());
    }

    private UpdatePostEvent createGroupUpdateEvent(int id, int groupID) {
        logger.info(groupID);
        Group group = modelBuilder.buildGroup(groupID);
        logger.info(group);
        final Activity activity = group.getActivity();
        final Game game = activity.getGame();
        Set<Integer> members = group.getMembers().stream().map(User::getId).collect(Collectors.toSet());
        logger.info("we get here? UPDATE POST");
        return new UpdatePostEvent(id, game.getId(), activity.getId(), members);
    }

    private GroupPostEvent createGroupEvent(int id){
        Integer groupID = postManager.getPostGroup(id);
        Integer ownerID = postManager.getUserPost(id);
        final Set<Integer> members = groupManager.getGroupMembers(groupID).stream()
                .filter(x -> !x.equals(ownerID))
                .collect(Collectors.toSet());
        return new GroupPostEvent(id, members);
    }

    public void deleteUserPost(int userID){
        final Integer userPost = postManager.getUserPost(userID);
        logger.info("userPost: " + userPost);
        if(userPost != null) deletePost(userPost);
    }

    public void notifyPostUpdate(int postID){
        Integer groupID = postManager.getPostGroup(postID);
        logger.info("groupID: " + groupID);
        if(groupID != null) updatePostEvent.fire(createGroupUpdateEvent(postID, groupID));
    }
}