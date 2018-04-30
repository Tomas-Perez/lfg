package restapi.game.resource;

import org.codehaus.plexus.util.StringUtils;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.model.Activity;
import persistence.model.Game;
import restapi.ApiTest;
import restapi.activity.model.CreateActivityJSON;
import restapi.game.model.ActivityJSON;
import restapi.game.model.CreateGameJSON;
import restapi.game.model.GameJSON;
import restapi.game.model.UpdateGameJSON;
import util.ManagerUtil;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class GameResourceTest extends ApiTest {

    @Test
    public void create(@ArquillianResteasyResource("games") final WebTarget webTarget) throws Exception{
        final String overwatch = "Overwatch";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGameJSON(overwatch));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(webTarget, gameTarget);

        final Response getResponse = RequestUtil.get(gameTarget, token);

        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        GameJSON actual = RequestUtil.parseResponse(getResponse, GameJSON.class);

        GameJSON expected = new GameJSON(Integer.parseInt(id), overwatch);

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("games/1") final WebTarget webTarget) throws Exception{
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("games/1") final WebTarget webTarget) throws Exception{
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void updateGame(@ArquillianResteasyResource("games") final WebTarget webTarget) throws Exception{
        final String overwatch = "Overwatch";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGameJSON(overwatch));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);

        final String gow = "God of War";
        final String image = "Dog";
        final Response updateResponse = RequestUtil.post(gameTarget, token, new UpdateGameJSON(gow, image));

        assertThat(updateResponse.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));

        final Response getResponse = RequestUtil.get(gameTarget, token);
        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        GameJSON actual = RequestUtil.parseResponse(getResponse, GameJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, gameTarget);
        GameJSON expected = new GameJSON(Integer.parseInt(id), gow);

        assertThat(actual, is(expected));
    }

    @Test
    public void updateGameExc(@ArquillianResteasyResource("games") final WebTarget webTarget) throws Exception{
        final String overwatch = "Overwatch";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGameJSON(overwatch));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);

        final String gow = "God of War";
        RequestUtil.post(webTarget, token, new CreateGameJSON(gow));
        final String image = "Dog";

        final Response updateResponse = RequestUtil.post(gameTarget, token, new UpdateGameJSON(gow, image));

        assertThat(updateResponse.getStatus(), is(Response.Status.CONFLICT.getStatusCode()));

        final Response getResponse = RequestUtil.get(gameTarget, token);
        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        GameJSON actual = RequestUtil.parseResponse(getResponse, GameJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, gameTarget);
        GameJSON expected = new GameJSON(Integer.parseInt(id), overwatch);

        assertThat(actual, is(expected));
    }

    @Test
    public void getAll(@ArquillianResteasyResource("games") final WebTarget webTarget) throws Exception{
        final Response response = RequestUtil.get(webTarget, token);

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        List<GameJSON> games = RequestUtil.parseListResponse(response, GameJSON.class);

        assertTrue(games.isEmpty());

        final String name1 = "God of war";
        int id1 = addGame(webTarget, name1);
        final String name2 = "Overwatch";
        int id2 = addGame(webTarget, name2);
        final String name3 = "FIFA";
        int id3 = addGame(webTarget, name3);

        GameJSON gameJSON1 = new GameJSON(id1, name1);
        GameJSON gameJSON2 = new GameJSON(id2, name2);
        GameJSON gameJSON3 = new GameJSON(id3, name3);

        final Response response2 = RequestUtil.get(webTarget, token);

        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<GameJSON> games2 = RequestUtil.parseListResponse(response2, GameJSON.class);

        assertFalse(games2.isEmpty());
        assertTrue(games2.contains(gameJSON1));
        assertTrue(games2.contains(gameJSON2));
        assertTrue(games2.contains(gameJSON3));
    }
}