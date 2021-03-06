package api.rest.post.resource;

import api.rest.chatPlatform.model.ChatPlatformJSON;
import api.rest.gamePlatform.model.GamePlatformJSON;
import api.rest.post.model.FilterPostsJSON;
import common.postfilter.FilterData;
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

import java.util.*;

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
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, activityID, ownerID, null, null, null));
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
    public void addPlatformPost(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        final String ps4 = "PS4";
        int ps4ID = addGamePlatform(ps4);

        final String discord = "Discord";
        int discordID = addChatPlatform(discord);

        final String description = "posty post";
        final Set<Integer> gamePlatforms = Collections.singleton(ps4ID);
        final Set<Integer> chatPlatforms = Collections.singleton(discordID);
        CreatePostJSON createPostJSON =
                new CreatePostJSON(description, activityID, ownerID, null, gamePlatforms, chatPlatforms);

        int postID = addPost(createPostJSON);

        WebTarget postTarget = RequestUtil.newRelativeTarget(base, String.format("posts/%d", postID));

        final Response getResponse = RequestUtil.get(postTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        PostJSON actual = RequestUtil.parseResponse(getResponse, PostJSON.class);

        MemberJSON ownerJSON = new MemberJSON(ownerID, ownerName);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        GamePlatformJSON gamePlatformJSON = new GamePlatformJSON(ps4ID, ps4);
        ChatPlatformJSON chatPlatformJSON = new ChatPlatformJSON(discordID, discord);

        PostJSON expected = new PostJSON(postID, description, actual.getDate(), activityJSON, ownerJSON, null, Collections.singleton(chatPlatformJSON), Collections.singleton(gamePlatformJSON));

        assertThat(actual, is(expected));
    }
}