package api.rest.group.service;

import api.rest.chat.service.ChatService;
import persistence.entity.GroupEntity;
import persistence.manager.GroupManager;
import persistence.model.Group;
import persistence.model.ModelBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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
    private ModelBuilder modelBuilder;

    @Inject
    private ChatService chatService;

    public List<Group> getAll(){
        return groupManager.list()
                .stream()
                .map(modelBuilder::buildGroup)
                .collect(Collectors.toList());
    }

    public int newGroup(int slots, int activityID, int ownerID){
        GroupEntity group = new GroupEntity(slots, activityID, null, null, ownerID);
        final int groupID = groupManager.add(group);
        chatService.newGroupChat(groupID);
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
            groupManager.delete(id);
            chatService.deleteChat(getGroupChat(id));
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void addMember(int id, int userID){
        try {
            groupManager.addMemberToGroup(id, userID);
            chatService.addMember(getGroupChat(id), userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void removeMember(int id, int userID){
        try {
            groupManager.removeMemberFromGroup(id, userID);
            chatService.removeMember(getGroupChat(id), userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    private int getGroupChat(int groupID){
        return groupManager.getGroupChat(groupID);
    }
}