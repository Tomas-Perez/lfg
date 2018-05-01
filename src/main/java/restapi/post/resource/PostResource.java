package restapi.post.resource;
import persistence.model.Post;
import restapi.post.model.CreatePostJSON;
import restapi.post.model.PostJSON;
import restapi.post.service.PostService;

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

@Path("posts")
@RequestScoped
public class PostResource {
    @Inject
    private PostService service;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        List<Post> posts = service.getAll();
        List<PostJSON> postJSONS = posts.stream().map(PostJSON::new).collect(Collectors.toList());
        return Response.ok(postJSONS).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(CreatePostJSON postJSON){
        int id;
        if(postJSON.getGroupID() == null)
            id = service.newPost(postJSON.getDescription(), postJSON.getActivityID(), postJSON.getOwnerID());
        else
            id = service.newGroupPost(postJSON.getDescription(), postJSON.getGroupID());
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") int id){
        Post post = service.getPost(id);
        return Response.ok(new PostJSON(post)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        service.deletePost(id);
        return Response.noContent().build();
    }
}
