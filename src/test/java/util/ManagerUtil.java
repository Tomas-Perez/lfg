package util;

import persistence.manager.ActivityManager;
import persistence.manager.GameManager;
import persistence.manager.GroupManager;
import persistence.manager.UserManager;
import persistence.model.Activity;
import persistence.model.Game;
import persistence.model.Group;
import persistence.model.User;

import java.util.Optional;

/**
 * @author Tomas Perez Molina
 */
public class ManagerUtil {


    public static Game addGame(GameManager gameManager, String name){
        gameManager.addGame(name, null);
        Optional<Game> optional = gameManager.getByName(name);
        return optional.orElseThrow(() -> new RuntimeException("Game not saved"));
    }

    public static Activity addActivity(ActivityManager activityManager, String name, Game game){
        activityManager.addActivity(name, game);
        Optional<Activity> optional = activityManager.getActivity(name, game);
        return optional.orElseThrow(() -> new RuntimeException("Activity not saved"));
    }

    public static User addUser(UserManager userManager,
                                String username,
                                String password,
                                String email,
                                boolean admin)
    {
        userManager.addUser(username, password, email, admin);
        Optional<User> optional = userManager.getByEmail(email);
        return optional.orElseThrow(() -> new RuntimeException("User not saved"));
    }

    public static Group addGroup(GroupManager groupManager, int slots, Activity activity, User owner){
        int id = groupManager.addGroup(slots, activity, owner, null, null);
        final Group group = groupManager.listGroups().stream().findFirst().get();
        System.out.println(id);
        System.out.println(group.getId());
        return group;
    }
}
