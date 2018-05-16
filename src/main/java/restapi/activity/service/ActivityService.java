package restapi.activity.service;

import persistence.manager.ActivityManager;
import persistence.manager.patcher.ActivityPatcher;
import persistence.model.Activity;
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
public class ActivityService {

    @Inject
    private ActivityManager activityManager;

    @Inject
    private ModelBuilder modelBuilder;

    public List<Activity> getAll(){
        return activityManager.listActivities()
                .stream()
                .map(modelBuilder::buildActivity)
                .collect(Collectors.toList());
    }

    public int newActivity(String name, int gameID){
        return activityManager.addActivity(name, gameID);
    }

    public Activity getActivity(int id){
        try {
            return modelBuilder.buildActivity(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
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
        ActivityPatcher patcher = new ActivityPatcher.Builder()
                .withName(name)
                .withGame(gameID)
                .build();
        activityManager.updateActivity(id, patcher);
    }
}

