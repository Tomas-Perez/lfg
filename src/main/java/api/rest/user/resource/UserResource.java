package api.rest.user.resource;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import persistence.model.User;
import api.rest.user.model.*;
import api.rest.user.service.UserService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    public Response getUser(@PathParam("id") int id){
        User user = service.getBasicUser(id);
        return Response.ok(new BasicUserData(user)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteUser(id);
        return Response.noContent().build();
    }

    @GET
    public Response search(@QueryParam("search") String search){
        List<User> users = search == null? service.getAll() : service.searchUser(search);
        List<BasicUserData> userJSONS = users.stream().map(BasicUserData::new).collect(Collectors.toList());
        return Response.ok(userJSONS).build();
    }

    @GET
    @Path("me/friends")
    public Response getFriends(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> friends = service.getFriends(id);
        List<BasicUserData> memberJSONS = friends.stream().map(BasicUserData::new).collect(Collectors.toList());
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
        int senderID = service.getIDByEmail(principal.getName());
        final int receiverID = friendRequestJSON.getId();
        service.sendFriendRequest(senderID, receiverID);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("me/friend-requests/received")
    public Response getReceivedRequests(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> requests = service.getReceivedRequests(id);
        List<BasicUserData> memberJSONS = requests.stream().map(BasicUserData::new).collect(Collectors.toList());
        return Response.ok(memberJSONS).build();
    }

    @GET
    @Path("me/friend-requests/sent")
    public Response getSentRequests(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        List<User> requests = service.getSentRequests(id);
        List<BasicUserData> memberJSONS = requests.stream().map(BasicUserData::new).collect(Collectors.toList());
        return Response.ok(memberJSONS).build();
    }

    @GET
    @Path("me/image")
    @Produces("image/png")
    public Response getMyImage(){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        final byte[] imageArray = service.getImage(id);
        return Response.ok(new ByteArrayInputStream(imageArray)).build();
    }

    @GET
    @Path("{id}/image")
    @Produces("image/png")
    public Response getImage(@PathParam("id") int id){
        final byte[] imageArray = service.getImage(id);
        return Response.ok(new ByteArrayInputStream(imageArray)).build();
    }

    @POST
    @Path("me/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMyImage(@FormDataParam("file") InputStream uploadedInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileDetail){
        Principal principal = securityContext.getUserPrincipal();
        int id = service.getIDByEmail(principal.getName());
        service.uploadImage(id, uploadedInputStream);
        return Response.noContent().build();
    }
}
