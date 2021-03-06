package api.rest.chat.resource;

import api.rest.chat.model.*;
import api.rest.chat.service.ChatService;
import api.rest.user.service.UserService;
import persistence.model.Chat;
import persistence.model.User;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@RequestScoped
@Path("chats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatResource {

    @Inject
    private ChatService service;

    @Inject
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response getAll(){
        List<Chat> chats = service.getAll();
        List<ChatJSON> chatJSONS = chats.stream().map(ChatJSON::new).collect(Collectors.toList());
        return Response.ok(chatJSONS).build();
    }

    @POST
    public Response post(CreateChatJSON chatJSON){
        Principal principal = securityContext.getUserPrincipal();
        int senderID = userService.getIDByEmail(principal.getName());
        int id;
        switch (chatJSON.getType()){
            case GROUP:
                id = service.newGroupChat(chatJSON.getGroupID());
                break;
            case PRIVATE:
                id = service.newPrivateChat(senderID, chatJSON.getRecipient());
                break;
            default:
                throw new BadRequestException("Invalid type.");
        }
        final String idString = Integer.toString(id);
        URI path = uriInfo.getAbsolutePathBuilder().path(idString).build();
        return Response.created(path).entity(buildChatWsPath(id)).build();
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
        Chat chat = service.getChat(id);
        return Response.ok(new ChatJSON(chat)).build();
    }

    @POST
    @Path("{id}")
    public Response closeChat(@PathParam("id") int id, CloseChatJSON closeChat){
        if(closeChat.isClose()){
            service.closeChat(id, closeChat.getMemberID());
        }
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        service.deleteChat(id);
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

    private WebsocketPathJSON buildChatWsPath(int id){
        URI base = uriInfo.getBaseUri();
        String websocketPath = String.format("ws://%s:%s%swebsockets/chats/%d",
                base.getHost(), base.getPort(), base.getPath(), id);
        return new WebsocketPathJSON(websocketPath);
    }
}
