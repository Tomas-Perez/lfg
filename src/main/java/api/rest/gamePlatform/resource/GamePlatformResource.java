package api.rest.gamePlatform.resource;

import api.rest.gamePlatform.model.CreateGamePlatformJSON;
import api.rest.gamePlatform.model.UpdateGamePlatformJSON;
import api.rest.gamePlatform.service.GamePlatformService;
import api.rest.gamePlatform.model.GamePlatformJSON;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import persistence.model.GamePlatform;

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

@Path("game-platforms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class GamePlatformResource {

    @Inject
    private GamePlatformService service;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(){
        List<GamePlatform> gamePlatforms = service.getAll();
        List<GamePlatformJSON> gamePlatformJSONS = gamePlatforms.stream()
                .map(GamePlatformJSON::new)
                .collect(Collectors.toList());
        return Response.ok(gamePlatformJSONS).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    public Response post(CreateGamePlatformJSON gameJSON){
        int id = service.newGamePlatform(gameJSON.getName(), null);
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
        GamePlatform gamePlatform = service.getGamePlatform(id);
        return Response.ok(new GamePlatformJSON(gamePlatform)).build();
    }



    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteGamePlatform(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response update(@PathParam("id") int id, UpdateGamePlatformJSON updateJSON){
        service.updateGamePlatform(id, updateJSON.getName(), updateJSON.getImage());
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
    public Response uploadMyImage(@PathParam("id") int id,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileDetail){
        service.uploadImage(id, uploadedInputStream);
        return Response.noContent().build();
    }
}
