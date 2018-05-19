package restapi.group.resource;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.ApiTest;
import restapi.activity.model.ActivityJSON;
import restapi.activity.model.GameJSON;
import restapi.group.model.AddMemberJSON;
import restapi.user.model.GroupJSON;
import restapi.group.model.MemberJSON;
import restapi.user.model.UserData;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class GroupUserRelationResourceTest extends ApiTest {

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

        RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member1ID));
        RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member2ID));

        WebTarget ownerTarget = RequestUtil.newRelativeTarget(base, String.format("users/%d", ownerID));
        final Response getOwner = RequestUtil.get(ownerTarget, token);
        UserData actual = RequestUtil.parseResponse(getOwner, UserData.class);

        GroupJSON groupJSON = createExpectedJSON(gameID, gameName, activityID, activityName, ownerID, username, groupID, slots);
        UserData expectedOwnerData = new UserData(ownerID, username, email, false, Collections.singletonList(groupJSON), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertThat(actual, is(expectedOwnerData));

        WebTarget member1Target = RequestUtil.newRelativeTarget(base, String.format("users/%d", member1ID));
        final Response getMember1 = RequestUtil.get(member1Target, token);
        UserData actualMember1 = RequestUtil.parseResponse(getMember1, UserData.class);
        UserData expectedMember1Data = new UserData(member1ID, username1, email1, false, Collections.singletonList(groupJSON), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertThat(actualMember1, is(expectedMember1Data));

        WebTarget member2Target = RequestUtil.newRelativeTarget(base, String.format("users/%d", member2ID));
        final Response getMember2 = RequestUtil.get(member2Target, token);
        UserData actualMember2 = RequestUtil.parseResponse(getMember2, UserData.class);
        UserData expectedMember2Data = new UserData(member2ID, username2, email2, false, Collections.singletonList(groupJSON), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertThat(actualMember2, is(expectedMember2Data));
    }

    private GroupJSON createExpectedJSON(int gameID, String gameName,
                                         int activityID, String activityName,
                                         int ownerID, String username,
                                         int id, int slots){
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        MemberJSON memberJSON = new MemberJSON(ownerID, username);
        return new GroupJSON(id, slots, activityJSON, memberJSON);
    }
}
