package restapi.post.resource;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.ApiTest;
import restapi.activity.model.ActivityJSON;
import restapi.activity.model.GameJSON;
import restapi.group.model.MemberJSON;
import restapi.post.model.CreatePostJSON;
import restapi.post.model.GroupJSON;
import restapi.post.model.PostJSON;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class PostResourceTest extends ApiTest {

    @Test
    public void createSoloPost() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, activityID, ownerID, null));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        System.out.println(location);
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postTarget, token);

        System.out.println(getResponse.getEntity());
        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        PostJSON actual = RequestUtil.parseResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, null);

        assertThat(actual, is(expected));
    }

    @Test
    public void createGroupPost() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        final int slots = 5;
        int groupID = addGroup(slots, activityID, ownerID);

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, null, null, groupID));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postTarget, token);

        System.out.println(getResponse.getEntity());
        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        PostJSON actual = RequestUtil.parseResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        GroupJSON groupJSON = new GroupJSON(groupID, Collections.singletonList(ownerJSON));

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, groupJSON);

        assertThat(actual, is(expected));
    }
}