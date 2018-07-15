package api.rest.group.resource;

import api.rest.ApiTest;
import api.rest.chatPlatform.model.ChatPlatformJSON;
import api.rest.gamePlatform.model.GamePlatformJSON;
import api.rest.group.model.CreateGroupJSON;
import api.rest.group.model.GroupJSON;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class GroupPlatformsResourceTest extends ApiTest {

    @Test
    public void getGroupWithPlatforms(){
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

        CreateGroupJSON createGroupJSON = new CreateGroupJSON(5, activityID, ownerID, discordID, ps4ID);

        int groupID = addGroup(createGroupJSON);

        WebTarget postTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d", groupID));

        final Response getResponse = RequestUtil.get(postTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        assertThat(actual.getChatPlatform(), is(new ChatPlatformJSON(discordID, discord)));
        assertThat(actual.getGamePlatform(), is(new GamePlatformJSON(ps4ID, ps4)));
    }

    @Test
    public void getGroupWithNoPlatforms(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        CreateGroupJSON createGroupJSON = new CreateGroupJSON(5, activityID, ownerID, null, null);

        int groupID = addGroup(createGroupJSON);

        WebTarget postTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d", groupID));

        final Response getResponse = RequestUtil.get(postTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        assertNull(actual.getChatPlatform());
        assertNull(actual.getGamePlatform());
    }
}
