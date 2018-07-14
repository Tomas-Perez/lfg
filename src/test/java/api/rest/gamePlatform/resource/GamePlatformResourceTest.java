package api.rest.gamePlatform.resource;

import api.rest.ApiTest;
import api.rest.gamePlatform.model.CreateGamePlatformJSON;
import api.rest.gamePlatform.model.GamePlatformJSON;
import api.rest.gamePlatform.model.UpdateGamePlatformJSON;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class GamePlatformResourceTest extends ApiTest {

    @Test
    public void create(@ArquillianResteasyResource("game-platforms") final WebTarget webTarget){
        final String ps4 = "PS4";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGamePlatformJSON(ps4));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gamePlatformTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(webTarget, gamePlatformTarget);

        final Response getResponse = RequestUtil.get(gamePlatformTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GamePlatformJSON actual = RequestUtil.parseResponse(getResponse, GamePlatformJSON.class);

        GamePlatformJSON expected = new GamePlatformJSON(Integer.parseInt(id), ps4);

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("game-platforms/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("game-platforms/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void updateGame(@ArquillianResteasyResource("game-platforms") final WebTarget webTarget){
        final String ps4 = "PS4";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGamePlatformJSON(ps4));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gamePlatformTarget = RequestUtil.newTarget(location);

        final String xbox = "XBOX";
        final String image = "green X";
        final Response updateResponse = RequestUtil.post(gamePlatformTarget, token, new UpdateGamePlatformJSON(xbox, image));

        assertThat(updateResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse = RequestUtil.get(gamePlatformTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        GamePlatformJSON actual = RequestUtil.parseResponse(getResponse, GamePlatformJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, gamePlatformTarget);
        GamePlatformJSON expected = new GamePlatformJSON(Integer.parseInt(id), xbox);

        assertThat(actual, is(expected));
    }

    @Test
    public void updateGameExc(@ArquillianResteasyResource("game-platforms") final WebTarget webTarget){
        final String ps4 = "PS4";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGamePlatformJSON(ps4));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget gamePlatformTarget = RequestUtil.newTarget(location);

        final String xbox = "XBOX";
        RequestUtil.post(webTarget, token, new CreateGamePlatformJSON(xbox));
        final String image = "Green X";

        final Response updateResponse = RequestUtil.post(gamePlatformTarget, token, new UpdateGamePlatformJSON(xbox, image));

        assertThat(updateResponse.getStatus(), is(CONFLICT));

        final Response getResponse = RequestUtil.get(gamePlatformTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        GamePlatformJSON actual = RequestUtil.parseResponse(getResponse, GamePlatformJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, gamePlatformTarget);
        GamePlatformJSON expected = new GamePlatformJSON(Integer.parseInt(id), ps4);

        assertThat(actual, is(expected));
    }

    @Test
    public void getAll(@ArquillianResteasyResource("game-platforms") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);

        assertThat(response.getStatus(), is(OK));
        List<GamePlatformJSON> gamePlatforms = RequestUtil.parseListResponse(response, GamePlatformJSON.class);

        assertTrue(gamePlatforms.isEmpty());

        final String name1 = "PS4";
        int id1 = addGamePlatform(name1);
        final String name2 = "XBOX";
        int id2 = addGamePlatform(name2);
        final String name3 = "PC";
        int id3 = addGamePlatform(name3);

        GamePlatformJSON gamePlatformJSON1 = new GamePlatformJSON(id1, name1);
        GamePlatformJSON gamePlatformJSON2 = new GamePlatformJSON(id2, name2);
        GamePlatformJSON gamePlatformJSON3 = new GamePlatformJSON(id3, name3);

        final Response response2 = RequestUtil.get(webTarget, token);

        assertThat(response2.getStatus(), is(OK));

        List<GamePlatformJSON> gamePlatforms2 = RequestUtil.parseListResponse(response2, GamePlatformJSON.class);

        assertFalse(gamePlatforms2.isEmpty());
        assertTrue(gamePlatforms2.contains(gamePlatformJSON1));
        assertTrue(gamePlatforms2.contains(gamePlatformJSON2));
        assertTrue(gamePlatforms2.contains(gamePlatformJSON3));
    }
}