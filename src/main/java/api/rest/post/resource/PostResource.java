package api.rest.post.resource;
import api.common.postfilter.FilterParameterDecoder;
import common.postfilter.FilterPair;
import persistence.model.Post;
import api.rest.post.model.CreatePostJSON;
import api.rest.post.model.PostJSON;
import api.rest.post.service.PostService;

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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PostResource {
    @Inject
    private PostService service;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getPosts(@QueryParam("filter") List<String> filters){
        List<Post> posts;
        if(filters == null || filters.isEmpty()){
            posts = service.getAll();
        }
        else {
            FilterParameterDecoder decoder = new FilterParameterDecoder();
            List<FilterPair> filterPairs = filters.stream()
                    .map(decoder::decode)
                    .distinct()
                    .collect(Collectors.toList());
            posts = service.getFilteredPosts(filterPairs);
        }
        List<PostJSON> postJSONS = posts.stream().map(PostJSON::new).collect(Collectors.toList());
        return Response.ok(postJSONS).build();
    }

    @POST
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
    public Response get(@PathParam("id") int id){
        System.out.println(id);
        Post post = service.getPost(id);
        System.out.println(post);
        return Response.ok(new PostJSON(post)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        service.deletePost(id);
        return Response.noContent().build();
    }
}
