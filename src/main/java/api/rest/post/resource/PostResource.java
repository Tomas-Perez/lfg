package api.rest.post.resource;
import api.common.postfilter.FilterParameterDecoder;
import api.rest.post.model.FilterPostsJSON;
import common.postfilter.FilterData;
import persistence.model.Post;
import api.rest.post.model.CreatePostJSON;
import api.rest.post.model.PostJSON;
import api.rest.post.service.PostService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
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
            List<FilterData> filterDatas = filters.stream()
                    .map(decoder::decode)
                    .distinct()
                    .collect(Collectors.toList());
            posts = service.getFilteredPosts(filterDatas);
        }
        List<PostJSON> postJSONS = posts.stream().map(PostJSON::new).collect(Collectors.toList());
        return Response.ok(new FilterPostsJSON(postJSONS, buildWsPath())).build();
    }

    @POST
    public Response post(CreatePostJSON postJSON){
        int id;
        if(postJSON.getGroupID() == null)
            id = service.newPost(
                    postJSON.getDescription(),
                    postJSON.getActivityID(),
                    postJSON.getOwnerID(),
                    postJSON.getChatPlatforms(),
                    postJSON.getGamePlatforms());
        else
            id = service.newGroupPost(
                    postJSON.getDescription(),
                    postJSON.getGroupID(),
                    postJSON.getChatPlatforms(),
                    postJSON.getGamePlatforms());
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
        Post post = service.getPost(id);
        return Response.ok(new PostJSON(post)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        service.deletePost(id);
        return Response.noContent().build();
    }

    private String buildWsPath(){
        URI base = uriInfo.getBaseUri();
        final MultivaluedMap<String, String> queryParamMap = uriInfo.getQueryParameters();
        String queryParams = queryParamMap.entrySet().stream()
                .map(this::entryToQueryParam)
                .collect(Collectors.joining("&"));

        String baseWsPath = String.format("ws://%s:%s%swebsockets/posts",
                base.getHost(), base.getPort(), base.getPath());

        return addParams(baseWsPath, queryParams);
    }

    private String entryToQueryParam(Map.Entry<String, List<String>> entry){
        String queryParamName = entry.getKey();
        List<String> queryParamValues = entry.getValue();
        return queryParamValues.stream()
                .map(val -> String.format("%s=%s", queryParamName, val))
                .collect(Collectors.joining("&"));
    }

    private String addParams(String baseWsPath, String params){
        if(params.length() > 0){
            String strWithParams = String.format("%s?%s", baseWsPath, params);
            return String.format("%s&%s", strWithParams, "access-token=");
        }
        return String.format("%s?%s", baseWsPath, "access-token=");
    }
}
