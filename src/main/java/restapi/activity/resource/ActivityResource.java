package restapi.activity.resource;

import persistence.model.Activity;
import restapi.activity.model.ActivityJSON;
import restapi.activity.service.ActivityService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@Path("activities")
@RequestScoped
public class ActivityResource {

    @Inject
    private ActivityService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        List<Activity> activities = service.getAll();
        List<ActivityJSON> activityJSONS = activities.stream().map(ActivityJSON::new).collect(Collectors.toList());
        return Response.ok(activityJSONS).build();
    }
}