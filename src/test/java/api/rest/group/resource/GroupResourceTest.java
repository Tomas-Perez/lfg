package api.rest.group.resource;

import api.rest.user.model.ChatJSON;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import api.rest.ApiTest;
import api.rest.activity.model.ActivityJSON;
import api.rest.activity.model.GameJSON;
import api.rest.group.model.AddMemberJSON;
import api.rest.group.model.CreateGroupJSON;
import api.rest.group.model.GroupJSON;
import api.rest.group.model.MemberJSON;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class GroupResourceTest extends ApiTest {

    @Test
    public void create(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String username = "wewey";
        final String password = "123123";
        final String email = "wewey@lfg.com";
        int ownerID = addUser(username, password, email);
        final int slots = 5;

        final Response postResponse = RequestUtil.post(groupsTarget, token, new CreateGroupJSON(slots, activityID, ownerID, null, null));

        assertThat(postResponse.getStatus(), is(CREATED));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget groupTarget = RequestUtil.newTarget(location);
        final String id = RequestUtil.getRelativePathDiff(groupsTarget, groupTarget);

        final Response getResponse = RequestUtil.get(groupTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, username,
                Integer.parseInt(id), slots, actual.getChat()
        );

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("groups/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("groups/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void addRemoveMembers(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String username = "wewey";
        final String password = "123123";
        final String email = "wewey@lfg.com";
        final String username1 = "member1";
        final String password1 = "123123";
        final String email1 = "member1@lfg.com";
        final String username2 = "member2";
        final String password2 = "123123";
        final String email2 = "member2@lfg.com";
        int ownerID = addUser(username, password, email);
        int member1ID = addUser(username1, password1, email1);
        int member2ID = addUser(username2, password2, email2);
        final int slots = 5;

        int groupID = addGroup(slots, activityID, ownerID);

        WebTarget groupMembersTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d/members", groupID));

        final Response postResponse = RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member1ID));

        assertThat(postResponse.getStatus(), is(NO_CONTENT));

        final Response postResponse2 = RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member2ID));

        assertThat(postResponse2.getStatus(), is(NO_CONTENT));


        WebTarget groupTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d", groupID));

        final Response getResponse = RequestUtil.get(groupTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, username,
                groupID, slots, actual.getChat()
        );

        MemberJSON ownerJSON = new MemberJSON(ownerID, username);
        MemberJSON member1JSON = new MemberJSON(member1ID, username1);
        MemberJSON member2JSON = new MemberJSON(member2ID, username2);

        expected.setMembers(new HashSet<>(Arrays.asList(ownerJSON, member1JSON, member2JSON)));

        assertThat(actual, is(expected));

        WebTarget groupMember1Target = RequestUtil.newRelativeTarget(
                base,
                String.format("groups/%d/members/%d", groupID, member1ID)
        );

        final Response deleteResponse = RequestUtil.delete(groupMember1Target, token);

        assertThat(deleteResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse2 = RequestUtil.get(groupTarget, token);

        assertThat(getResponse2.getStatus(), is(OK));

        actual = RequestUtil.parseResponse(getResponse2, GroupJSON.class);

        expected.setMembers(new HashSet<>(Arrays.asList(ownerJSON, member2JSON)));

        assertThat(actual, is(expected));
    }

    @Test
    public void removeOnlyMember(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String username = "wewey";
        final String password = "123123";
        final String email = "wewey@lfg.com";
        int ownerID = addUser(username, password, email);
        final int slots = 5;

        int groupID = addGroup(slots, activityID, ownerID);

        WebTarget groupTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d", groupID));

        final Response getResponse = RequestUtil.get(groupTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        GroupJSON actual = RequestUtil.parseResponse(getResponse, GroupJSON.class);

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, username,
                groupID, slots, actual.getChat()
        );

        MemberJSON ownerJSON = new MemberJSON(ownerID, username);

        expected.setMembers(new HashSet<>(Collections.singletonList(ownerJSON)));

        assertThat(actual, is(expected));

        WebTarget groupMember1Target = RequestUtil.newRelativeTarget(
                base,
                String.format("groups/%d/members/%d", groupID, ownerID)
        );

        final Response deleteResponse = RequestUtil.delete(groupMember1Target, token);

        assertThat(deleteResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse2 = RequestUtil.get(groupTarget, token);

        assertThat(getResponse2.getStatus(), is(NOT_FOUND));
    }

    private GroupJSON createExpectedJSON(int gameID, String gameName,
                                         int activityID, String activityName,
                                         int ownerID, String username,
                                         int id, int slots, ChatJSON chatJSON){
        GameJSON gameJSON = new GameJSON(gameID, gameName);
        ActivityJSON activityJSON = new ActivityJSON(activityID, activityName, gameJSON);
        MemberJSON memberJSON = new MemberJSON(ownerID, username);
        return new GroupJSON(id, slots, activityJSON, memberJSON, Collections.singleton(memberJSON), chatJSON, null, null);
    }
    @Test
    public void getAll(){
        List<UserDetails> group1Members = Arrays.asList(
                new UserDetails("member11", "132", "member11@mail.com"),
                new UserDetails("member21", "132", "member21@mail.com"),
                new UserDetails("member31", "132", "member31@mail.com"),
                new UserDetails("member41", "132", "member41@mail.com")
        );
        GroupJSON group1 = fullExpected("Overwatch1", "Ranked1", new UserDetails("owner1", "123", "owner1@mail.com"), group1Members, null);

        List<UserDetails> group2Members = Arrays.asList(
                new UserDetails("member12", "132", "member12@mail.com"),
                new UserDetails("member22", "132", "member22@mail.com"),
                new UserDetails("member32", "132", "member32@mail.com"),
                new UserDetails("member42", "132", "member42@mail.com")
        );
        GroupJSON group2 = fullExpected("Overwatch2", "Ranked2", new UserDetails("owner2", "123", "owner2@mail.com"), group2Members, null);

        List<UserDetails> group3Members = Arrays.asList(
                new UserDetails("member13", "132", "member13@mail.com"),
                new UserDetails("member23", "132", "member23@mail.com"),
                new UserDetails("member33", "132", "member33@mail.com"),
                new UserDetails("member43", "132", "member43@mail.com")
        );
        GroupJSON group3 = fullExpected("Overwatch3", "Ranked3", new UserDetails("owner3", "123", "owner3@mail.com"), group3Members, null);

        final Response getAllResponse = RequestUtil.get(groupsTarget, token);
        List<GroupJSON> actual = RequestUtil.parseListResponse(getAllResponse, GroupJSON.class);

        assertFalse(actual.isEmpty());
        assertThat(actual.size(), is(3));
        assertTrue(actual.contains(group1));
        assertTrue(actual.contains(group2));
        assertTrue(actual.contains(group3));
    }

    private GroupJSON fullExpected(String gameName, String activityName, UserDetails owner, List<UserDetails> members, ChatJSON chatJSON){
        int gameID = addGame(gameName);
        int activityID = addActivity(activityName, gameID);
        int ownerID = addUser(owner.getUsername(), owner.getPassword(), owner.getEmail());
        Set<MemberJSON> membersJSON = members.stream().map(this::userDetailToMemberJSON).collect(Collectors.toSet());
        List<AddMemberJSON> addMemberJSONS = membersJSON.stream().map(memberJSON -> new AddMemberJSON(memberJSON.getId())).collect(Collectors.toList());
        final int slots = 5;
        int groupID = addGroup(slots, activityID, ownerID);

        WebTarget groupMembersTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d/members", groupID));

        addMemberJSONS.forEach(addMemberJSON -> RequestUtil.post(groupMembersTarget, token, addMemberJSON));

        GroupJSON expected = createExpectedJSON(
                gameID, gameName,
                activityID, activityName,
                ownerID, owner.getUsername(),
                groupID, slots, chatJSON
        );

        membersJSON.add(new MemberJSON(ownerID, owner.getUsername()));

        expected.setMembers(membersJSON);

        return expected;
    }

    private MemberJSON userDetailToMemberJSON(UserDetails details){
        int id = addUser(details.getUsername(), details.getPassword(), details.getEmail());
        return new MemberJSON(id, details.getUsername());
    }

    private class UserDetails{
        String username;
        String password;
        String email;

        UserDetails(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }

        String getEmail() {
            return email;
        }
    }
}