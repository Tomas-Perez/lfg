package api.rest.chatPlatform.resource;

import api.rest.ApiTest;
import api.rest.chatPlatform.model.ChatPlatformJSON;
import api.rest.chatPlatform.model.CreateChatPlatformJSON;
import api.rest.chatPlatform.model.UpdateChatPlatformJSON;
import api.rest.game.model.CreateGameJSON;
import api.rest.game.model.GameJSON;
import api.rest.game.model.UpdateGameJSON;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.model.ChatPlatform;
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
public class ChatPlatformResourceTest extends ApiTest {

    @Test
    public void create(@ArquillianResteasyResource("chat-platforms") final WebTarget webTarget){
        final String ps4 = "Discord";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateChatPlatformJSON(ps4));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget chatPlatformTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(webTarget, chatPlatformTarget);

        final Response getResponse = RequestUtil.get(chatPlatformTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        ChatPlatformJSON actual = RequestUtil.parseResponse(getResponse, ChatPlatformJSON.class);

        ChatPlatformJSON expected = new ChatPlatformJSON(Integer.parseInt(id), ps4);

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("chat-platforms/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("chat-platforms/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void updateGame(@ArquillianResteasyResource("chat-platforms") final WebTarget webTarget){
        final String discord = "Discord";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateChatPlatformJSON(discord));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget chatPlatformTarget = RequestUtil.newTarget(location);

        final String mumble = "Mumble";
        final String image = "Headphones";
        final Response updateResponse = RequestUtil.post(chatPlatformTarget, token, new UpdateChatPlatformJSON(mumble, image));

        assertThat(updateResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse = RequestUtil.get(chatPlatformTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        ChatPlatformJSON actual = RequestUtil.parseResponse(getResponse, ChatPlatformJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, chatPlatformTarget);
        ChatPlatformJSON expected = new ChatPlatformJSON(Integer.parseInt(id), mumble);

        assertThat(actual, is(expected));
    }

    @Test
    public void updateGameExc(@ArquillianResteasyResource("chat-platforms") final WebTarget webTarget){
        final String discord = "Discord";
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateChatPlatformJSON(discord));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget chatPlatformTarget = RequestUtil.newTarget(location);

        final String mumble = "Mumble";
        RequestUtil.post(webTarget, token, new CreateChatPlatformJSON(mumble));
        final String image = "Headphones";

        final Response updateResponse = RequestUtil.post(chatPlatformTarget, token, new UpdateChatPlatformJSON(mumble, image));

        assertThat(updateResponse.getStatus(), is(CONFLICT));

        final Response getResponse = RequestUtil.get(chatPlatformTarget, token);
        assertThat(getResponse.getStatus(), is(OK));

        ChatPlatformJSON actual = RequestUtil.parseResponse(getResponse, ChatPlatformJSON.class);

        final String id = RequestUtil.getRelativePathDiff(webTarget, chatPlatformTarget);
        ChatPlatformJSON expected = new ChatPlatformJSON(Integer.parseInt(id), discord);

        assertThat(actual, is(expected));
    }

    @Test
    public void getAll(@ArquillianResteasyResource("chat-platforms") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);

        assertThat(response.getStatus(), is(OK));
        List<ChatPlatformJSON> chatPlatforms = RequestUtil.parseListResponse(response, ChatPlatformJSON.class);

        assertTrue(chatPlatforms.isEmpty());

        final String name1 = "Discord";
        int id1 = addChatPlatform(name1);
        final String name2 = "Mumble";
        int id2 = addChatPlatform(name2);
        final String name3 = "Skype";
        int id3 = addChatPlatform(name3);

        ChatPlatformJSON chatPlatform1 = new ChatPlatformJSON(id1, name1);
        ChatPlatformJSON chatPlatform2 = new ChatPlatformJSON(id2, name2);
        ChatPlatformJSON chatPlatform3 = new ChatPlatformJSON(id3, name3);

        final Response response2 = RequestUtil.get(webTarget, token);

        assertThat(response2.getStatus(), is(OK));

        List<ChatPlatformJSON> chatPlatforms2 = RequestUtil.parseListResponse(response2, ChatPlatformJSON.class);

        assertFalse(chatPlatforms2.isEmpty());
        assertTrue(chatPlatforms2.contains(chatPlatform1));
        assertTrue(chatPlatforms2.contains(chatPlatform2));
        assertTrue(chatPlatforms2.contains(chatPlatform3));
    }
}