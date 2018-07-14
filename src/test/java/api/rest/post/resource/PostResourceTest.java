package api.rest.post.resource;

import api.rest.post.model.FilterPostsJSON;
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
import java.util.HashSet;
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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, activityID, ownerID, null, new HashSet<>(), new HashSet<>()));
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

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, actual.getDate(), activityJSON, ownerJSON, null, new HashSet<>(), new HashSet<>());

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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, null, null, groupID, new HashSet<>(), new HashSet<>()));

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

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, groupJSON, new HashSet<>(), new HashSet<>());

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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, activityID, ownerID, null, new HashSet<>(), new HashSet<>()));

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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, null, null, groupID, new HashSet<>(), new HashSet<>()));

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
                Collections.singletonList(new api.rest.user.model.GroupJSON(groupID)), actual.getFriends(),
                actual.getReceivedRequests(), actual.getSentRequests(),
                actual.getChats(), new api.rest.user.model.PostJSON(Integer.parseInt(id)));

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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, activityID, ownerID, null, new HashSet<>(), new HashSet<>()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postsTarget, token);

        FilterPostsJSON actual = RequestUtil.parseResponse(getResponse, FilterPostsJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);

        PostJSON expected = new PostJSON(Integer.parseInt(id), description, null, activityJSON, ownerJSON, null, new HashSet<>(), new HashSet<>());

        List<PostJSON> expectedList = Collections.singletonList(expected);

        assertThat(actual.getPosts(), is(expectedList));
        System.out.println(actual.getSocketPath());

        final Response getResponseFiltered = RequestUtil.get(postsTarget.queryParam("filter", Integer.toString(gameID)), token);

        FilterPostsJSON actualFiltered = RequestUtil.parseResponse(getResponseFiltered, FilterPostsJSON.class);

        assertThat(actualFiltered.getPosts(), is(expectedList));
        System.out.println(actualFiltered.getSocketPath());

        final Response getResponseFiltered2 = RequestUtil.get(postsTarget.queryParam("filter", Integer.toString(gameID) + ":" + Integer.toString(activityID)), token);

        FilterPostsJSON actualFiltered2 = RequestUtil.parseResponse(getResponseFiltered2, FilterPostsJSON.class);

        assertThat(actualFiltered2.getPosts(), is(expectedList));
        System.out.println(actualFiltered2.getSocketPath());

        final Response getResponseFiltered3 = RequestUtil.get(postsTarget.queryParam("filter", 0 + ":" + 0), token);

        FilterPostsJSON actualFiltered3 = RequestUtil.parseResponse(getResponseFiltered3, FilterPostsJSON.class);

        assertThat(actualFiltered3.getPosts(), is(new ArrayList<>()));

        System.out.println(actualFiltered3.getSocketPath());
    }
}