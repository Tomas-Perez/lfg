package api.rest.group.service;

import api.common.event.group.*;
import api.common.event.post.DeleteGroupPost;
import api.common.event.post.GroupPostEvent;
import api.common.event.post.NewGroupPost;
import api.rest.chat.service.ChatService;
import api.rest.post.service.PostService;
import api.rest.user.model.BasicUserData;
import persistence.entity.GroupEntity;
import persistence.entity.UserEntity;
import persistence.manager.GroupManager;
import persistence.manager.UserManager;
import persistence.model.Group;
import persistence.model.ModelBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupService {

    @Inject
    private GroupManager groupManager;

    @Inject
    private UserManager userManager;

    @Inject
    private PostService postService;

    @Inject
    private ModelBuilder modelBuilder;

    @Inject
    private ChatService chatService;

    @Inject
    @NewMember
    private Event<MemberEvent> newMemberEvent;

    @Inject
    @DeleteMember
    private Event<MemberEvent> deleteMemberEvent;

    @Inject
    @NewOwner
    private Event<NewOwnerEvent> newOwnerEvent;

    @Inject
    @DeleteGroup
    private Event<GroupEvent> deleteGroupEvent;

    @Inject
    @NewGroup
    private Event<GroupEvent> newGroupEvent;

    @Inject
    @NewGroupPost
    private Event<GroupPostEvent> newGroupPostEvent;

    @Inject
    @DeleteGroupPost
    private Event<GroupPostEvent> deleteGroupPostEvent;

    public List<Group> getAll(){
        return groupManager.list()
                .stream()
                .map(modelBuilder::buildGroup)
                .collect(Collectors.toList());
    }

    public int newGroup(int slots, int activityID, int ownerID, Integer chatPlatformID, Integer gamePlatformID){
        GroupEntity group = new GroupEntity(slots, activityID, chatPlatformID, gamePlatformID, ownerID);
        final int groupID = groupManager.add(group);
        chatService.newGroupChat(groupID);
        newGroupEvent.fire(new GroupEvent(groupID, Collections.singleton(ownerID)));
        return groupID;
    }

    public Group getGroup(int id){
        try {
            return modelBuilder.buildGroup(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        } catch (Exception exc){
            exc.printStackTrace();
            throw new NotFoundException();
        }
    }

    public void wipe(){
        groupManager.wipe();
    }

    public void deleteGroup(int id){
        try {
            final List<Integer> groupMembers = groupManager.getGroupMembers(id);
            final int groupChat = getGroupChat(id);
            chatService.deleteChat(groupChat);
            groupManager.delete(id);
            deleteGroupEvent.fire(new GroupEvent(id, new HashSet<>(groupMembers)));
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void addMember(int id, int userID){
        try {
            final int groupChat = getGroupChat(id);
            groupManager.addMemberToGroup(id, userID);
            chatService.addMember(groupChat, userID);
            postService.deleteUserPost(userID);

            notifyNewMember(id, userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void removeMember(int id, int userID){
        try {
            final int groupChat = getGroupChat(id);
            final Integer postID = groupManager.getGroupPost(id);
            chatService.removeMember(groupChat, userID);
            groupManager.removeMemberFromGroup(id, userID);

            notifyRemovedMember(id, userID, postID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void replaceOwner(int id, int newOwnerID){
        try {
            int oldOwnerID = groupManager.replaceOwner(id, newOwnerID);
            newOwnerEvent.fire(new NewOwnerEvent(id, oldOwnerID, newOwnerID));
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    private void notifyRemovedMember(int id, int userID, Integer postID) {
        final Set<Integer> singleton = Collections.singleton(userID);
        if(postID != null) deleteGroupPostEvent.fire(new GroupPostEvent(postID, singleton));
        deleteMemberEvent.fire(createMemberEvent(id, userID));
        deleteGroupEvent.fire(new GroupEvent(id, singleton));
    }

    private void notifyNewMember(int id, int userID) {
        final Integer postID = groupManager.getGroupPost(id);
        final Set<Integer> singleton = Collections.singleton(userID);
        if(postID != null) newGroupPostEvent.fire(new GroupPostEvent(postID, singleton));
        newMemberEvent.fire(createMemberEvent(id, userID));
        newGroupEvent.fire(new GroupEvent(id, singleton));
    }

    private int getGroupChat(int groupID){
        return groupManager.getGroupChat(groupID);
    }

    private MemberEvent createMemberEvent(int groupID, int userID){
        UserEntity user = userManager.get(userID);
        BasicUserData data = new BasicUserData(user.getId(), user.getUsername());
        return new MemberEvent(groupID, data);
    }
}