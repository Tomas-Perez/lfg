package restapi.activity.resource;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.ApiTest;
import restapi.game.model.ActivityJSON;
import restapi.game.model.GameJSON;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class ActivityGameRelationResourceTest extends ApiTest {

    @Test
    public void getAll() throws Exception{
        final WebTarget gamesTarget = RequestUtil.newRelativeTarget(base, "games");
        final WebTarget activitiesTarget = RequestUtil.newRelativeTarget(base, "activities");

        final String name1 = "God of war";
        int gameID1 = addGame(gamesTarget, name1);
        final String name2 = "Overwatch";
        int gameID2 = addGame(gamesTarget, name2);

        GameJSON gameJSON1 = new GameJSON(gameID1, name1);
        GameJSON gameJSON2 = new GameJSON(gameID2, name2);

        final String activityName1 = "Ranked";
        final String activityName2 = "Casual";
        final String activityName3 = "Campaign";

        int activityID1 = addActivity(activitiesTarget, activityName1, gameID2);
        int activityID2 = addActivity(activitiesTarget, activityName2, gameID2);
        int activityID3 = addActivity(activitiesTarget, activityName3, gameID1);
        int activityID4 = addActivity(activitiesTarget, activityName2, gameID1);

        ActivityJSON activityJSON1 = new ActivityJSON(activityID1, activityName1);
        ActivityJSON activityJSON2 = new ActivityJSON(activityID2, activityName2);
        ActivityJSON activityJSON3 = new ActivityJSON(activityID3, activityName3);
        ActivityJSON activityJSON4 = new ActivityJSON(activityID4, activityName2);

        List<ActivityJSON> game1Activities = Arrays.asList(activityJSON3, activityJSON4);
        List<ActivityJSON> game2Activities = Arrays.asList(activityJSON1, activityJSON2);
        gameJSON1.setActivities(game1Activities);
        gameJSON2.setActivities(game2Activities);

        final Response response = RequestUtil.get(gamesTarget, token);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<GameJSON> games = RequestUtil.parseListResponse(response, GameJSON.class);

        assertFalse(games.isEmpty());
        assertTrue(games.contains(gameJSON1));
        assertTrue(games.contains(gameJSON2));
    }
}
