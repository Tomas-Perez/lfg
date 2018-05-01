package restapi.group.resource;

import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.GroupManager;
import persistence.model.Activity;
import persistence.model.Game;
import persistence.model.Group;
import persistence.model.User;
import restapi.ApiTest;
import restapi.activity.model.ActivityJSON;
import restapi.activity.model.GameJSON;
import restapi.group.model.CreateGroupJSON;
import restapi.group.model.GroupJSON;
import restapi.group.model.UserJSON;
import util.ManagerUtil;
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
public class GroupResourceTest extends ApiTest {

    @Test
    public void create() throws Exception {
//        final String gameName = "Overwatch";
//        int gameID = addGame(gameName);
//        final String activityName = "Ranked";
//        int activityID = addActivity(activityName, gameID);
//        final String username = "wewey";
//        final String password = "123123";
//        final String email = "wewey@lfg.com";
//        int ownerID = addUser(username, password, email);
//        final int slots = 5;
//
//        final Response postResponse = RequestUtil.post(groupsTarget, token, new CreateGroupJSON(slots, activityID, ownerID));
//
//        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));
//
//        final String location = postResponse.getHeaderString("Location");
//        final WebTarget groupTarget = RequestUtil.newTarget(location);
//        final String id = RequestUtil.getRelativePathDiff(groupsTarget, groupTarget);
//
//        final Response getResponse = RequestUtil.get(groupTarget, token);
//
//        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));
//
//        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);
//
//
//        ActivityJSON activityJSON = new ActivityJSON(activityID, )
//
//        GroupJSON expected = new GroupJSON(Integer.parseInt(id), slots, );
//
//        assertThat(actual, is(expected));
    }

    @Test
    public void getAll(@ArquillianResteasyResource("groups") final WebTarget webTarget) throws Exception{
//        final Response response = RequestUtil.get(webTarget, token);
//
//        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
//        List<GroupJSON> groups = RequestUtil.parseListResponse(response, GroupJSON.class);
//
//        assertTrue(groups.isEmpty());
//
//        Game game = ManagerUtil.addGame(gameManager, "God of war");
//        Activity activity = ManagerUtil.addActivity(activityManager, "Campaign", game);
//        User owner = ManagerUtil.addUser(userManager, "owner", "123", "owner@mail.com", true);
//        User member1 = ManagerUtil.addUser(userManager, "member1", "123", "member1@mail.com", false);
//        User member2 = ManagerUtil.addUser(userManager, "member2", "123", "member2@mail.com", false);
//
//        groupManager = new GroupManager(emp.createEntityManager());
//        Group group = ManagerUtil.addGroup(groupManager, 5, activity, userManager.getByEmail("owner@mail.com").get());
//        assertNotNull(group);
//        assertNotNull(groupManager);
//        groupManager.addMemberToGroup(group.getId(), member1);
//        groupManager.addMemberToGroup(group.getId(), member2);
//
//        GameJSON gameJSON = new GameJSON(game);
//        ActivityJSON activityJSON = new ActivityJSON(activity);
//        UserJSON ownerJSON = new UserJSON(owner);
//        UserJSON member1JSON = new UserJSON(member1);
//        UserJSON member2JSON = new UserJSON(member2);
//        GroupJSON groupJSON = new GroupJSON(group);
//
//        final Response response2 = RequestUtil.get(webTarget, token);
//
//        assertThat(response2.getStatus(), is(Response.Status.OK.getStatusCode()));
//
//        List<GroupJSON> groups2 = RequestUtil.parseListResponse(response2, GroupJSON.class);
//
//        assertFalse(groups2.isEmpty());
//        assertTrue(groups2.contains(groupJSON));
//
//        GroupJSON actual = groups2.get(0);
//        assertThat(actual, is(groupJSON));
    }
}