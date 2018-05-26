package api.rest.activity.resource;

import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import api.rest.ApiTest;
import api.rest.activity.model.ActivityJSON;
import api.rest.activity.model.CreateActivityJSON;
import api.rest.activity.model.GameJSON;
import api.rest.activity.model.UpdateActivityJSON;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class ActivityResourceTest extends ApiTest {

    @Test
    public void create() throws Exception{

        final String gameName = "Overwatch";
        int gameID = addGame(gameName);

        final String activityName = "Ranked";
        final Response postResponse = RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName, gameID)
        );

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget activityTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(activitiesTarget, activityTarget);

        final Response getResponse = RequestUtil.get(activityTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GameJSON gameJSON = new GameJSON(gameID, gameName);

        ActivityJSON actual = RequestUtil.parseResponse(getResponse, ActivityJSON.class);

        ActivityJSON expected = new ActivityJSON(Integer.parseInt(id), activityName, gameJSON);

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("activities/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("activities/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void updateName() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);

        final String activityName = "Ranked";
        final String activityName2 = "Casual";

        final Response postResponse = RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName, gameID)
        );

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget activityTarget = RequestUtil.newTarget(location);

        final Response updateResponse = RequestUtil.post(activityTarget, token, new UpdateActivityJSON(activityName2, gameID));

        assertThat(updateResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse = RequestUtil.get(activityTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        ActivityJSON actual = RequestUtil.parseResponse(getResponse, ActivityJSON.class);
        GameJSON gameJSON = new GameJSON(gameID, gameName);

        final String id = RequestUtil.getRelativePathDiff(activitiesTarget, activityTarget);
        ActivityJSON expected = new ActivityJSON(Integer.parseInt(id), activityName2, gameJSON);

        assertThat(actual, is(expected));
    }

    @Test
    public void updateGame() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);

        final String gameName2 = "God of War";
        int gameID2 = addGame(gameName2);

        final String activityName = "Ranked";

        final Response postResponse = RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName, gameID)
        );

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget activityTarget = RequestUtil.newTarget(location);

        final Response updateResponse = RequestUtil.post(activityTarget, token, new UpdateActivityJSON(activityName, gameID2));

        assertThat(updateResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse = RequestUtil.get(activityTarget, token);
        System.out.println(activityTarget.getUri());
        assertThat(getResponse.getStatus(), is(OK));

        ActivityJSON actual = RequestUtil.parseResponse(getResponse, ActivityJSON.class);
        GameJSON gameJSON = new GameJSON(gameID2, gameName2);

        final String id = RequestUtil.getRelativePathDiff(activitiesTarget, activityTarget);
        ActivityJSON expected = new ActivityJSON(Integer.parseInt(id), activityName, gameJSON);

        assertThat(actual, is(expected));
    }

    @Test
    public void updateConflictExc() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);

        final String gameName2 = "God of War";
        int gameID2 = addGame(gameName2);

        final String gameName3 = "FIFA";
        addGame(gameName3);

        final String activityName = "Ranked";
        final String activityName2 = "Casual";

        final Response postResponse = RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName, gameID)
        );
        assertThat(postResponse.getStatus(), is(CREATED));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget activityTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(activitiesTarget, activityTarget);

        RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName, gameID2)
        );
        RequestUtil.post(
                activitiesTarget,
                token,
                new CreateActivityJSON(activityName2, gameID)
        );

        //Conflict by changing game
        final Response updateResponse = RequestUtil.post(activityTarget, token, new UpdateActivityJSON(activityName, gameID2));
        assertThat(updateResponse.getStatus(), is(CONFLICT));

        final Response getResponse = RequestUtil.get(activityTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        ActivityJSON actual = RequestUtil.parseResponse(getResponse, ActivityJSON.class);
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON expected = new ActivityJSON(Integer.parseInt(id), activityName, gameJSON);

        assertThat(actual, is(expected));

        //Conflict by changing name
        final Response updateResponse2 = RequestUtil.post(activityTarget, token, new UpdateActivityJSON(activityName2, gameID));
        assertThat(updateResponse2.getStatus(), is(CONFLICT));

        final Response getResponse2 = RequestUtil.get(activityTarget, token);
        assertThat(getResponse2.getStatus(), is(OK));

        ActivityJSON actual2 = RequestUtil.parseResponse(getResponse2, ActivityJSON.class);
        GameJSON gameJSON2 = new GameJSON(gameID, gameName);
        ActivityJSON expected2 = new ActivityJSON(Integer.parseInt(id), activityName, gameJSON2);

        assertThat(actual2, is(expected2));
    }

    @Test
    public void getAll() throws Exception{

        final Response response = RequestUtil.get(activitiesTarget, token);

        assertThat(response.getStatus(), is(OK));
        List<ActivityJSON> activities = RequestUtil.parseListResponse(response, ActivityJSON.class);

        assertTrue(activities.isEmpty());

        final String name1 = "God of war";
        int gameID1 = addGame(name1);
        final String name2 = "Overwatch";
        int gameID2 = addGame(name2);

        GameJSON gameJSON1 = new GameJSON(gameID1, name1);
        GameJSON gameJSON2 = new GameJSON(gameID2, name2);

        final String activityName1 = "Ranked";
        final String activityName2 = "Casual";
        final String activityName3 = "Campaign";

        int activityID1 = addActivity(activityName1, gameID2);
        int activityID2 = addActivity(activityName2, gameID2);
        int activityID3 = addActivity(activityName3, gameID1);
        int activityID4 = addActivity(activityName2, gameID1);

        ActivityJSON activityJSON1 = new ActivityJSON(activityID1, activityName1, gameJSON2);
        ActivityJSON activityJSON2 = new ActivityJSON(activityID2, activityName2, gameJSON2);
        ActivityJSON activityJSON3 = new ActivityJSON(activityID3, activityName3, gameJSON1);
        ActivityJSON activityJSON4 = new ActivityJSON(activityID4, activityName2, gameJSON1);



        final Response response2 = RequestUtil.get(activitiesTarget, token);

        assertThat(response2.getStatus(), is(OK));

        List<ActivityJSON> activities2 = RequestUtil.parseListResponse(response2, ActivityJSON.class);

        assertFalse(activities2.isEmpty());
        assertTrue(activities2.contains(activityJSON1));
        assertTrue(activities2.contains(activityJSON2));
        assertTrue(activities2.contains(activityJSON3));
        assertTrue(activities2.contains(activityJSON4));
    }

}