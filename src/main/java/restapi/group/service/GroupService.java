package restapi.group.service;

import persistence.manager.ActivityManager;
import persistence.manager.GroupManager;
import persistence.manager.UserManager;
import persistence.model.Activity;
import persistence.model.Group;
import persistence.model.ModelBuilder;
import persistence.model.User;

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
        return groupManager.listGroups()
                .stream()
                .map(modelBuilder::buildGroup)
                .collect(Collectors.toList());
    }

    public int newGroup(int slots, int activityID, int ownerID){
        return groupManager.addGroup(slots, activityID, ownerID, null, null);
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
        groupManager.wipeAllRecords();
    }

    public void deleteGroup(int id){
        try {
            groupManager.deleteGroup(id);
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