package api.rest.chatPlatform.resource;

import api.rest.chatPlatform.model.CreateChatPlatformJSON;
import api.rest.chatPlatform.model.UpdateChatPlatformJSON;
import api.rest.chatPlatform.service.ChatPlatformService;
import api.rest.chatPlatform.model.ChatPlatformJSON;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import persistence.model.ChatPlatform;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@Path("chat-platforms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ChatPlatformResource {

    @Inject
    private ChatPlatformService service;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(){
        List<ChatPlatform> chatPlatforms = service.getAll();
        List<ChatPlatformJSON> chatPlatformJSONS = chatPlatforms.stream()
                .map(ChatPlatformJSON::new)
                .collect(Collectors.toList());
        return Response.ok(chatPlatformJSONS).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    public Response post(CreateChatPlatformJSON chatPlatformJSON){
        int id = service.newChatPlatform(chatPlatformJSON.getName(), null);
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
        ChatPlatform chatPlatform = service.getChatPlatform(id);
        return Response.ok(new ChatPlatformJSON(chatPlatform)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteChatPlatform(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response update(@PathParam("id") int id, UpdateChatPlatformJSON updateJSON){
        service.updateChatPlatform(id, updateJSON.getName(), updateJSON.getImage());
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/image")
    @Produces("image/png")
    public Response getImage(@PathParam("id") int id){
        final byte[] imageArray = service.getImage(id);
        return Response.ok(new ByteArrayInputStream(imageArray)).build();
    }

    @POST
    @Path("{id}/image")
    @RolesAllowed({"ADMIN"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadImage(@PathParam("id") int id,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileDetail){
        service.uploadImage(id, uploadedInputStream);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}/image")
    @RolesAllowed({"ADMIN"})
    public Response deleteImage(@PathParam("id") int id){
        service.deleteImage(id);
        return Response.noContent().build();
    }
}
