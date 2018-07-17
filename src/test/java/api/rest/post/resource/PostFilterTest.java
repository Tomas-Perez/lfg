package api.rest.post.resource;

import api.rest.ApiTest;
import api.rest.activity.model.ActivityJSON;
import api.rest.activity.model.GameJSON;
import api.rest.group.model.MemberJSON;
import api.rest.post.model.CreatePostJSON;
import api.rest.post.model.FilterPostsJSON;
import api.rest.post.model.PostJSON;
import common.postfilter.FilterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class PostFilterTest extends ApiTest {

    private static final Logger logger = LogManager.getLogger(PostFilterTest.class);

    @Test
    public void filterPostsByActivity(){
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

        Set<Integer> expectedList = Collections.singleton(Integer.parseInt(id));
        Set<Integer> actualIDs = getPostIDs(actual);

        assertThat(actualIDs, is(expectedList));
        logger.info(actual.getSocketPath());

        final Response getResponseFiltered = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d", FilterData.ACTIVITY_DELIM, gameID)), token);

        FilterPostsJSON actualFiltered = RequestUtil.parseResponse(getResponseFiltered, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs = getPostIDs(actualFiltered);

        assertThat(actualFilteredIDs, is(expectedList));
        logger.info(actualFiltered.getSocketPath());

        final Response getResponseFiltered2 = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d:%d", FilterData.ACTIVITY_DELIM, gameID, activityID)), token);

        FilterPostsJSON actualFiltered2 = RequestUtil.parseResponse(getResponseFiltered2, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs2 = getPostIDs(actualFiltered2);

        assertThat(actualFilteredIDs2, is(expectedList));
        logger.info(actualFiltered2.getSocketPath());

        final Response getResponseFiltered3 = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d:%d", FilterData.ACTIVITY_DELIM, 0, 0)), token);

        FilterPostsJSON actualFiltered3 = RequestUtil.parseResponse(getResponseFiltered3, FilterPostsJSON.class);
        Set<Integer> actualFilteredIDs3 = getPostIDs(actualFiltered3);

        assertThat(actualFilteredIDs3, is(new HashSet<>()));

        logger.info(actualFiltered3.getSocketPath());
    }

    @Test
    public void filterPostsByType(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token,
                new CreatePostJSON(
                        description,
                        activityID,
                        ownerID,
                        null,
                        new HashSet<>(),
                        new HashSet<>()
                )
        );

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postsTarget, token);

        FilterPostsJSON actual = RequestUtil.parseResponse(getResponse, FilterPostsJSON.class);

        Set<Integer> expectedList = Collections.singleton(Integer.parseInt(id));
        Set<Integer> actualIDs = getPostIDs(actual);

        assertThat(actualIDs, is(expectedList));
        logger.info(actual.getSocketPath());

        final Response getResponseFiltered = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%s", FilterData.TYPE_DELIM, FilterData.PostType.LFG)), token);

        FilterPostsJSON actualFiltered = RequestUtil.parseResponse(getResponseFiltered, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs = getPostIDs(actualFiltered);

        assertThat(actualFilteredIDs, is(expectedList));
        logger.info(actualFiltered.getSocketPath());

        final Response getResponseFiltered2 = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%s", FilterData.TYPE_DELIM, FilterData.PostType.LFM)), token);

        FilterPostsJSON actualFiltered2 = RequestUtil.parseResponse(getResponseFiltered2, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs2 = getPostIDs(actualFiltered2);

        assertThat(actualFilteredIDs2, is(new HashSet<>()));

        logger.info(actualFiltered2.getSocketPath());
    }

    @Test
    public void filterPostsByPlatform(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        int gamePlatformID = addGamePlatform("PS4");
        int chatPlatformID = addChatPlatform("Discord");

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token,
                new CreatePostJSON(
                        description,
                        activityID,
                        ownerID,
                        null,
                        Collections.singleton(gamePlatformID),
                        Collections.singleton(chatPlatformID)
                )
        );

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(postsTarget, postTarget);

        final Response getResponse = RequestUtil.get(postsTarget, token);

        FilterPostsJSON actual = RequestUtil.parseResponse(getResponse, FilterPostsJSON.class);

        Set<Integer> expectedList = Collections.singleton(Integer.parseInt(id));
        Set<Integer> actualIDs = getPostIDs(actual);

        assertThat(actualIDs, is(expectedList));
        logger.info(actual.getSocketPath());

        final Response getResponseFiltered = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d", FilterData.GAME_PLATFORM_DELIM, gamePlatformID)), token);

        FilterPostsJSON actualFiltered = RequestUtil.parseResponse(getResponseFiltered, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs = getPostIDs(actualFiltered);

        assertThat(actualFilteredIDs, is(expectedList));
        logger.info(actualFiltered.getSocketPath());

        final Response getResponseFiltered2 = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d", FilterData.CHAT_PLATFORM_DELIM, chatPlatformID)), token);

        FilterPostsJSON actualFiltered2 = RequestUtil.parseResponse(getResponseFiltered2, FilterPostsJSON.class);

        Set<Integer> actualFilteredIDs2 = getPostIDs(actualFiltered2);

        assertThat(actualFilteredIDs2, is(expectedList));
        logger.info(actualFiltered2.getSocketPath());

        final Response getResponseFiltered3 = RequestUtil.get(postsTarget.queryParam("filter", String.format("%c%d,%c%d", FilterData.CHAT_PLATFORM_DELIM, gamePlatformID, FilterData.GAME_PLATFORM_DELIM, chatPlatformID)), token);

        FilterPostsJSON actualFiltered3 = RequestUtil.parseResponse(getResponseFiltered3, FilterPostsJSON.class);
        Set<Integer> actualFilteredIDs3 = getPostIDs(actualFiltered3);

        assertThat(actualFilteredIDs3, is(new HashSet<>()));

        logger.info(actualFiltered3.getSocketPath());
    }

    private Set<Integer> getPostIDs(FilterPostsJSON json){
        return json.getPosts().stream().map(PostJSON::getId).collect(Collectors.toSet());
    }
}
