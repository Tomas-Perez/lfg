package api.rest.chat.resource;

import api.rest.chat.model.AddMemberJSON;
import api.rest.chat.model.ChatJSON;
import api.rest.chat.model.CreateChatJSON;
import api.rest.chat.model.WebsocketPathJSON;
import api.rest.chat.service.ChatService;
import persistence.model.Chat;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
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

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(){
        List<Chat> chats = service.getAll();
        List<ChatJSON> chatJSONS = chats.stream().map(ChatJSON::new).collect(Collectors.toList());
        return Response.ok(chatJSONS).build();
    }

    @POST
    public Response post(CreateChatJSON chatJSON) throws URISyntaxException {
        int id = service.newChat(chatJSON.getMembers());
        final String idString = Integer.toString(id);
        URI path = uriInfo.getAbsolutePathBuilder().path(idString).build();
        URI base = uriInfo.getBaseUri();
        String websocketPath = String.format("ws://%s:%s%swebsockets/chats/%d",
                base.getHost(), base.getPort(), base.getPath(), id);
        return Response.created(path).entity(new WebsocketPathJSON(websocketPath)).build();
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
}
