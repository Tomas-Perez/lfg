package restapi.activity.service;

import persistence.manager.ActivityManager;
import persistence.model.Activity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class ActivityService {

    @Inject
    private ActivityManager manager;

    public List<Activity> getAll(){
        return manager.listActivities();
    }
}

