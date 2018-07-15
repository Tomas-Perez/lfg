package api.rest.group.resource;

import api.rest.group.model.ReplaceOwnerJSON;
import persistence.model.Group;
import api.rest.group.model.AddMemberJSON;
import api.rest.group.model.CreateGroupJSON;
import api.rest.group.model.GroupJSON;
import api.rest.group.service.GroupService;

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
@Path("groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class GroupResource {

    @Inject
    private GroupService service;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(){
        List<Group> groups = service.getAll();
        List<GroupJSON> groupJSONS = groups.stream().map(GroupJSON::new).collect(Collectors.toList());
        return Response.ok(groupJSONS).build();
    }

    @POST
    public Response post(CreateGroupJSON groupJSON){
        int id = service.newGroup(
                groupJSON.getSlots(),
                groupJSON.getActivityID(),
                groupJSON.getOwnerID(),
                groupJSON.getChatPlatform(),
                groupJSON.getGamePlatform()
        );
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
        Group group = service.getGroup(id);
        return Response.ok(new GroupJSON(group)).build();
    }

    @POST
    @Path("{id}")
    public Response replaceOwner(@PathParam("id") int id, ReplaceOwnerJSON ownerJSON){
        service.replaceOwner(id, ownerJSON.getNewOwnerID());
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        service.deleteGroup(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/members")
    public Response addMember(@PathParam("id") int id, AddMemberJSON addMemberJSON){
        service.addMember(id, addMemberJSON.getId());
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}/members/{memberID}")
    public Response removeMember(@PathParam("id") int id, @PathParam("memberID") int memberID){
        service.removeMember(id, memberID);
        return Response.noContent().build();
    }
}