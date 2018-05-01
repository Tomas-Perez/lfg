package restapi.group.service;

import persistence.manager.ActivityManager;
import persistence.manager.GroupManager;
import persistence.manager.UserManager;
import persistence.model.Activity;
import persistence.model.Group;
import persistence.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupService {

    @Inject
    private GroupManager groupManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private UserManager userManager;

    public List<Group> getAll(){
        return groupManager.listGroups();
    }

    public int newGroup(int slots, int activityID, int ownerID){
        final Activity activity = getActivity(activityID);
        final User owner = getUser(ownerID);
        return groupManager.addGroup(slots, activity, owner, null, null);
    }

    public Group getGroup(int id){
        final Group group = groupManager.getGroup(id);
        if(group == null) throw new NotFoundException();
        return group;
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
        final User user = getUser(userID);
        groupManager.addMemberToGroup(id, user);
    }

    public void removeMember(int id, int userID){
        final User user = getUser(userID);
        groupManager.removeMemberFromGroup(id, user);
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
}