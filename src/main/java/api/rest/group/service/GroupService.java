package api.rest.group.service;

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

    public List<Group> getAll(){
        return groupManager.list()
                .stream()
                .map(modelBuilder::buildGroup)
                .collect(Collectors.toList());
    }

    public int newGroup(int slots, int activityID, int ownerID){
        GroupEntity group = new GroupEntity(slots, activityID, null, null, ownerID);
        return groupManager.add(group);
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
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void addMember(int id, int userID){
        groupManager.addMemberToGroup(id, userID);
    }

    public void removeMember(int id, int userID){
        groupManager.removeMemberFromGroup(id, userID);
    }
}