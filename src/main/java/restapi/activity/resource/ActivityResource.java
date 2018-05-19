package restapi.activity.resource;

import persistence.model.Activity;
import restapi.activity.model.ActivityJSON;
import restapi.activity.model.CreateActivityJSON;
import restapi.activity.model.UpdateActivityJSON;
import restapi.activity.service.ActivityService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
@Path("activities")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ActivityResource {

    @Inject
    private ActivityService service;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(){
        List<Activity> activities = service.getAll();
        List<ActivityJSON> activityJSONS = activities.stream().map(ActivityJSON::new).collect(Collectors.toList());
        return Response.ok(activityJSONS).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    public Response post(CreateActivityJSON activityJSON){
        int id = service.newActivity(activityJSON.getName(), activityJSON.getGameID());
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(id)).build();
        return Response.created(path).build();
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    public Response wipe(){
        service.wipe();
        return Response.noContent().build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") int id){
        System.out.println(id);
        Activity activity = service.getActivity(id);
        return Response.ok(new ActivityJSON(activity)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteActivity(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response update(@PathParam("id") int id, UpdateActivityJSON updateJSON){
        service.updateActivity(id, updateJSON.getName(), updateJSON.getGameID());
        return Response.noContent().build();
    }
}