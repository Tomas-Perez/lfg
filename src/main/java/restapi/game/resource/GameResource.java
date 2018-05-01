package restapi.game.resource;

import persistence.model.Game;
import restapi.game.model.CreateGameJSON;
import restapi.game.model.GameJSON;
import restapi.game.model.UpdateGameJSON;
import restapi.game.service.GameService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.inject.Inject;
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

@Path("games")
@RequestScoped
public class GameResource {

    @Inject
    private GameService service;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        List<Game> games = service.getAll();
        List<GameJSON> gameJSONS = games.stream().map(GameJSON::new).collect(Collectors.toList());
        return Response.ok(gameJSONS).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(CreateGameJSON gameJSON){
        int id = service.newGame(gameJSON.getName(), null);
        URI path = uriInfo.getAbsolutePathBuilder().path(Integer.toString(id)).build();
        return Response.created(path).header("Access-Control-Expose-Headers", "Location").build();
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
        Game game = service.getGame(id);
        return Response.ok(new GameJSON(game)).build();
    }



    @DELETE
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") int id){
        service.deleteGame(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}")
    @RolesAllowed({"ADMIN"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, UpdateGameJSON updateJSON){
        service.updateGame(id, updateJSON.getName(), updateJSON.getImage());
        return Response.noContent().build();
    }
}