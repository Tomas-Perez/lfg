package api.rest.group.resource;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import api.rest.ApiTest;
import api.rest.group.model.AddMemberJSON;
import api.rest.user.model.BasicUserData;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
        BasicUserData actual = RequestUtil.parseResponse(getOwner, BasicUserData.class);

        BasicUserData expectedOwnerData = new BasicUserData(ownerID, username);

        assertThat(actual, is(expectedOwnerData));

        WebTarget member1Target = RequestUtil.newRelativeTarget(base, String.format("users/%d", member1ID));
        final Response getMember1 = RequestUtil.get(member1Target, token);
        BasicUserData actualMember1 = RequestUtil.parseResponse(getMember1, BasicUserData.class);
        BasicUserData expectedMember1Data = new BasicUserData(member1ID, username1);

        assertThat(actualMember1, is(expectedMember1Data));

        WebTarget member2Target = RequestUtil.newRelativeTarget(base, String.format("users/%d", member2ID));
        final Response getMember2 = RequestUtil.get(member2Target, token);
        BasicUserData actualMember2 = RequestUtil.parseResponse(getMember2, BasicUserData.class);
        BasicUserData expectedMember2Data = new BasicUserData(member2ID, username2);

        assertThat(actualMember2, is(expectedMember2Data));
    }
}
