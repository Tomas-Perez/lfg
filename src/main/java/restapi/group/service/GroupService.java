package restapi.group.service;

import persistence.manager.GroupManager;
import persistence.model.Group;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
@ApplicationScoped
public class GroupService {

    @Inject
    private GroupManager manager;

    public List<Group> getAll(){
        return manager.listGroups();
    }
}