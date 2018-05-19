package restapi.user.resource;

import persistence.model.User;
import restapi.group.model.MemberJSON;
import restapi.user.model.ConfirmRequestJSON;
import restapi.user.model.FriendRequestJSON;
import restapi.user.model.UpdateUserJSON;
import restapi.user.model.UserData;
import restapi.user.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@Path("users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Context
    private SecurityContext securityContext;

    @Inject
    private UserService service;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("me")
    public Response getMe() {
        Principal principal = securityContext.getUserPrincipal();
        User user = service.getUserByEmail(principal.getName());
        return Response.ok(new UserData(user)).build();
    }

    @POST
    @Path("me")
    public Response updateMe(UpdateUserJSON updateJSON){
        Principal principal = securityContext.getUserPrincipal();
        User user = service.getUserByEmail(principal.getName());
        service.updateUser(
                user.getId(),
                updateJSON.getUsername(),
                updateJSON.getEmail(),
                updateJSON.getPassword()
        );
        return Response.noContent().build();
    }

    @DELETE
    @Path("me")
    public Response deleteMe(){
        Principal principal = securityContext.getUserPrincipal();
        User user = service.getUserByEmail(principal.getName());
        service.deleteUser(user.getId());
        return Response.noContent().build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response getUser(@PathParam("id") int id){
        User user = service.getUser(id);
        return Response.ok(new UserData(user)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteUser(id);
        return Response.noContent().build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getAll(){
        List<User> users = service.getAll();
        List<UserData> userJSONS = users.stream().map(UserData::new).collect(Collectors.toList());
        return Response.ok(userJSONS).build();
    }

    @GET
    @Path("me/friends")
    public Response getFriends(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> friends = service.getFriends(id);
        List<MemberJSON> memberJSONS = friends.stream().map(MemberJSON::new).collect(Collectors.toList());
        return Response.ok(memberJSONS).build();
    }

    @POST
    @Path("me/friends")
    public Response confirmFriend(ConfirmRequestJSON confirmJSON){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        service.confirmFriendRequest(id, confirmJSON.getId());
        return Response.ok().build();
    }

    @DELETE
    @Path("me/friends/{friendId}")
    public Response removeFriend(@PathParam("friendId") int friendId){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        service.removeFriend(id, friendId);
        return Response.noContent().build();
    }

    @POST
    @Path("me/friend-requests")
    public Response sendFriendRequest(FriendRequestJSON friendRequestJSON){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        final int otherID = friendRequestJSON.getId();
        service.sendFriendRequest(id, otherID);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("me/friend-requests/received")
    public Response getReceivedRequests(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> requests = service.getReceivedRequests(id);
        List<MemberJSON> memberJSONS = requests.stream().map(MemberJSON::new).collect(Collectors.toList());
        return Response.ok(memberJSONS).build();
    }

    @GET
    @Path("me/friend-requests/sent")
    public Response getSentRequests(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> requests = service.getSentRequests(id);
        List<MemberJSON> memberJSONS = requests.stream().map(MemberJSON::new).collect(Collectors.toList());
        return Response.ok(memberJSONS).build();
    }
}
