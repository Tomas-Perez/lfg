package api.rest.group.service;

import api.common.event.group.*;
import api.rest.chat.service.ChatService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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
            newMemberEvent.fire(createMemberEvent(id, userID));
            newGroupEvent.fire(new GroupEvent(id, Collections.singleton(userID)));
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void removeMember(int id, int userID){
        try {
            final int groupChat = getGroupChat(id);
            chatService.removeMember(groupChat, userID);
            groupManager.removeMemberFromGroup(id, userID);
            deleteMemberEvent.fire(createMemberEvent(id, userID));
            deleteGroupEvent.fire(new GroupEvent(id, Collections.singleton(userID)));
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

    private int getGroupChat(int groupID){
        return groupManager.getGroupChat(groupID);
    }

    private MemberEvent createMemberEvent(int groupID, int userID){
        UserEntity user = userManager.get(userID);
        BasicUserData data = new BasicUserData(user.getId(), user.getUsername());
        return new MemberEvent(groupID, data);
    }
}