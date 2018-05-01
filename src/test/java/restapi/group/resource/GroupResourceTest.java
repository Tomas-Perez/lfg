package restapi.group.resource;

import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.ApiTest;
import restapi.activity.model.ActivityJSON;
import restapi.activity.model.GameJSON;
import restapi.group.model.AddMemberJSON;
import restapi.group.model.CreateGroupJSON;
import restapi.group.model.GroupJSON;
import restapi.group.model.MemberJSON;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class GroupResourceTest extends ApiTest {

    @Test
    public void create() throws Exception {
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String username = "wewey";
        final String password = "123123";
        final String email = "wewey@lfg.com";
        int ownerID = addUser(username, password, email);
        final int slots = 5;

        final Response postResponse = RequestUtil.post(groupsTarget, token, new CreateGroupJSON(slots, activityID, ownerID));

        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget groupTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(groupsTarget, groupTarget);

        final Response getResponse = RequestUtil.get(groupTarget, token);

        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, username,
                Integer.parseInt(id), slots
        );

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("groups/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("groups/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
    }

    @Test
    public void addRemoveMembers() throws Exception{
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String username = "wewey";
        final String password = "123123";
        final String email = "wewey@lfg.com";
        final String username1 = "member2";
        final String password1 = "123123";
        final String email1 = "member2@lfg.com";
        final String username2 = "member3";
        final String password2 = "123123";
        final String email2 = "member3@lfg.com";
        int ownerID = addUser(username, password, email);
        int member1ID = addUser(username1, password1, email1);
        int member2ID = addUser(username2, password2, email2);
        final int slots = 5;

        int groupID = addGroup(slots, activityID, ownerID);

        WebTarget groupMembersTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d/members", groupID));

        final Response postResponse = RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member1ID));

        assertThat(postResponse.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));

        final Response postResponse2 = RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member2ID));

        assertThat(postResponse2.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));


        WebTarget groupTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d", groupID));

        final Response getResponse = RequestUtil.get(groupTarget, token);

        assertThat(getResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, username,
                groupID, slots
        );

        MemberJSON ownerJSON = new MemberJSON(ownerID, username);
        MemberJSON member1JSON = new MemberJSON(member1ID, username1);
        MemberJSON member2JSON = new MemberJSON(member2ID, username2);

        expected.setMembers(Arrays.asList(ownerJSON, member1JSON, member2JSON));

        assertThat(actual, is(expected));

        WebTarget groupMember1Target = RequestUtil.newRelativeTarget(
                base,
                String.format("groups/%d/members/%d", groupID, member1ID)
        );

        final Response deleteResponse = RequestUtil.delete(groupMember1Target, token);

        assertThat(deleteResponse.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));

        final Response getResponse2 = RequestUtil.get(groupTarget, token);

        assertThat(getResponse2.getStatus(), is(Response.Status.OK.getStatusCode()));

        actual = RequestUtil.parseResponse(getResponse2, GroupJSON.class);

        expected.setMembers(Arrays.asList(ownerJSON, member2JSON));

        assertThat(actual, is(expected));
    }

    private GroupJSON createExpectedJSON(int gameID, String gameName,
                                         int activityID, String activityName,
                                         int ownerID, String username,
                                         int id, int slots){
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        MemberJSON memberJSON = new MemberJSON(ownerID, username);
        return new GroupJSON(id, slots, activityJSON, memberJSON, Collections.singletonList(memberJSON));
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
//        MemberJSON ownerJSON = new MemberJSON(owner);
//        MemberJSON member1JSON = new MemberJSON(member1);
//        MemberJSON member2JSON = new MemberJSON(member2);
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