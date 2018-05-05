package restapi.activity.service;

import persistence.manager.ActivityManager;
import persistence.manager.GameManager;
import persistence.manager.patcher.ActivityPatcher;
import persistence.model.Activity;
import persistence.model.Game;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class ActivityService {

    @Inject
    private ActivityManager activityManager;

    @Inject
    private GameManager gameManager;

    public List<Activity> getAll(){
        return activityManager.listActivities();
    }

    public int newActivity(String name, int gameID){
        final Game game = getGame(gameID);
        return activityManager.addActivity(name, game);
    }

    public Activity getActivity(int id){
        final Activity activity = activityManager.getActivity(id);
        System.out.println(activity);
        if(activity == null) throw new NotFoundException();
        return activity;
    }

    public void wipe(){
        activityManager.wipeAllRecords();
    }

    public void deleteActivity(int id){
        try {
            activityManager.deleteActivity(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void updateActivity(int id, String name, int gameID){
        final Game game = getGame(gameID);
        System.out.println(game);
        ActivityPatcher patcher = new ActivityPatcher.Builder()
                .withName(name)
                .withGame(game)
                .build();
        activityManager.updateActivity(id, patcher);
    }

    private Game getGame(int gameID){
        Game game = gameManager.getGame(gameID);
        if(game == null) throw new NotFoundException("Game not found");
        return game;
    }
}

