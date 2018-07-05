package api.rest.post.resource;

import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import api.rest.ApiTest;
import api.rest.activity.model.ActivityJSON;
import api.rest.activity.model.GameJSON;
import api.rest.group.model.MemberJSON;
import api.rest.post.model.CreatePostJSON;
import api.rest.post.model.GroupJSON;
import api.rest.post.model.PostJSON;
import api.rest.security.authentication.model.AuthenticationToken;
import api.rest.user.model.UserData;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class PostResourceTest extends ApiTest {

    @Test
    public void createSoloPost(){
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
        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        PostJSON actual = RequestUtil.parseResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, null);

        assertThat(actual, is(expected));
    }

    @Test
    public void createGroupPost(){
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

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        PostJSON actual = RequestUtil.parseResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        GroupJSON groupJSON = new GroupJSON(groupID, slots, Collections.singletonList(ownerJSON));

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, groupJSON);

        assertThat(actual, is(expected));
    }

    @Test
    public void addsPostToUser(){
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

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        AuthenticationToken ownerToken = RequestUtil.getToken(signInTarget, ownerEmail, ownerPass);

        final Response ownerMeResponse = RequestUtil.get(meTarget, ownerToken);
        assertThat(ownerMeResponse.getStatus(), is(OK));

        UserData actual = RequestUtil.parseResponse(ownerMeResponse, UserData.class);

        UserData expected = new UserData(
                ownerID, ownerName,
                ownerEmail, false,
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new api.rest.user.model.PostJSON(Integer.parseInt(id)));

        assertThat(expected, is(actual));
    }

    @Test
    public void addsGroupPostToUser(){
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

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        AuthenticationToken ownerToken = RequestUtil.getToken(signInTarget, ownerEmail, ownerPass);

        final Response ownerMeResponse = RequestUtil.get(meTarget, ownerToken);
        assertThat(ownerMeResponse.getStatus(), is(OK));

        UserData actual = RequestUtil.parseResponse(ownerMeResponse, UserData.class);

        UserData expected = new UserData(
                ownerID, ownerName,
                ownerEmail, false,
                Collections.singletonList(new api.rest.user.model.GroupJSON(groupID)), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new api.rest.user.model.PostJSON(Integer.parseInt(id)));

        assertThat(expected, is(actual));
    }

    @Test
    public void filterPosts(){
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

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postsTarget, token);

        List<PostJSON> actual = RequestUtil.parseListResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, null);

        List<PostJSON> expectedList = Collections.singletonList(expected);

        assertThat(actual, is(expectedList));

        final Response getResponseFiltered = RequestUtil.get(postsTarget.queryParam("filter", Integer.toString(gameID)), token);

        List<PostJSON> actualFiltered = RequestUtil.parseListResponse(getResponseFiltered, PostJSON.class);

        assertThat(actualFiltered, is(expectedList));

        final Response getResponseFiltered2 = RequestUtil.get(postsTarget.queryParam("filter", Integer.toString(gameID) + ":" + Integer.toString(activityID)), token);

        List<PostJSON> actualFiltered2 = RequestUtil.parseListResponse(getResponseFiltered2, PostJSON.class);

        assertThat(actualFiltered2, is(expectedList));

        final Response getResponseFiltered3 = RequestUtil.get(postsTarget.queryParam("filter", 0 + ":" + 0), token);

        List<PostJSON> actualFiltered3 = RequestUtil.parseListResponse(getResponseFiltered3, PostJSON.class);

        assertThat(actualFiltered3, is(new ArrayList<>()));
    }
}