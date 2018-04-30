package restapi.group.resource;

import persistence.model.Group;
import restapi.group.model.GroupJSON;
import restapi.group.service.GroupService;

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
@Path("groups")
@RequestScoped
public class GroupResource {

    @Inject
    private GroupService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        List<Group> groups = service.getAll();
        List<GroupJSON> groupJSONS = groups.stream().map(GroupJSON::new).collect(Collectors.toList());
        return Response.ok(groupJSONS).build();
    }
}