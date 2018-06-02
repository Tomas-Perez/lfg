package api.rest.user.resource;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import api.rest.ApiTest;
import api.rest.security.authentication.model.AuthenticationToken;
import api.rest.user.model.BasicUserData;
import api.rest.user.model.ConfirmRequestJSON;
import api.rest.user.model.FriendRequestJSON;
import api.rest.user.model.UserData;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class UserResourceTest extends ApiTest {

    @Test
    public void getMe(){
        String username = "username";
        String email = "email";
        String password = "password";
        int id = addUser(username, password, email);

        final AuthenticationToken userToken = RequestUtil.getToken(signInTarget, email, password);

        final Response meResponse = RequestUtil.get(meTarget, userToken);
        UserData meData = RequestUtil.parseResponse(meResponse, UserData.class);

        UserData expected = new UserData(id, username, email, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);

        assertThat(meData, is(expected));
    }

    @Test
    public void sendFriendRequests(){
        String username1 = "username1";
        String email1 = "email1";
        String password1 = "password1";
        int id1 = addUser(username1, password1, email1);

        String username2 = "username2";
        String email2 = "email2";
        String password2 = "password2";
        int id2 = addUser(username2, password2, email2);

        String username3 = "username3";
        String email3 = "email3";
        String password3 = "password3";
        int id3 = addUser(username3, password3, email3);

        final AuthenticationToken user1Token = RequestUtil.getToken(signInTarget, email1, password1);
        final AuthenticationToken user2Token = RequestUtil.getToken(signInTarget, email2, password2);
        final AuthenticationToken user3Token = RequestUtil.getToken(signInTarget, email3, password3);

        final WebTarget requestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests");
        final WebTarget sentRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/sent");
        final WebTarget receivedRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/received");

        final Response sendRequestResponse1 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id2));
        System.out.println(sendRequestResponse1.readEntity(String.class));
        assertThat(sendRequestResponse1.getStatus(), is(CREATED));

        final Response sendRequestResponse2 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id3));
        assertThat(sendRequestResponse2.getStatus(), is(CREATED));

        List<BasicUserData> expected = Arrays.asList(
                new BasicUserData(id2, username2),
                new BasicUserData(id3, username3)
        );

        final Response sentRequestsResponse = RequestUtil.get(sentRequestsTarget, user1Token);
        assertThat(sentRequestsResponse.getStatus(), is(OK));

        List<BasicUserData> actual = RequestUtil.parseListResponse(sentRequestsResponse, BasicUserData.class);

        assertThat(new HashSet<>(actual), is(new HashSet<>(expected)));

        final Response receivedRequestsResponse1 = RequestUtil.get(receivedRequestsTarget, user2Token);
        assertThat(receivedRequestsResponse1.getStatus(), is(OK));

        final Response receivedRequestsResponse2 = RequestUtil.get(receivedRequestsTarget, user3Token);
        assertThat(receivedRequestsResponse2.getStatus(), is(OK));

        List<BasicUserData> user1Singleton = Collections.singletonList(new BasicUserData(id1, username1));
        Set<BasicUserData> user1Set = new HashSet<>(user1Singleton);

        List<BasicUserData> actualReceived1 = RequestUtil.parseListResponse(receivedRequestsResponse1, BasicUserData.class);
        List<BasicUserData> actualReceived2 = RequestUtil.parseListResponse(receivedRequestsResponse2, BasicUserData.class);

        assertThat(new HashSet<>(actualReceived1), is(user1Set));
        assertThat(new HashSet<>(actualReceived2), is(user1Set));
    }

    @Test
    public void confirmFriendRequest(){
        String username1 = "username1";
        String email1 = "email1";
        String password1 = "password1";
        int id1 = addUser(username1, password1, email1);

        String username2 = "username2";
        String email2 = "email2";
        String password2 = "password2";
        int id2 = addUser(username2, password2, email2);

        String username3 = "username3";
        String email3 = "email3";
        String password3 = "password3";
        int id3 = addUser(username3, password3, email3);

        final AuthenticationToken user1Token = RequestUtil.getToken(signInTarget, email1, password1);
        final AuthenticationToken user2Token = RequestUtil.getToken(signInTarget, email2, password2);
        final AuthenticationToken user3Token = RequestUtil.getToken(signInTarget, email3, password3);

        final WebTarget requestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests");
        final WebTarget sentRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/sent");
        final WebTarget receivedRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/received");

        final Response sendRequestResponse1 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id2));
        assertThat(sendRequestResponse1.getStatus(), is(CREATED));

        final Response sendRequestResponse2 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id3));
        assertThat(sendRequestResponse2.getStatus(), is(CREATED));

        final WebTarget friendsTarget = RequestUtil.newRelativeTarget(base, "users/me/friends");

        final ConfirmRequestJSON confirmJSON = new ConfirmRequestJSON(id1);
        final Response confirmRequestResponse2 = RequestUtil.post(friendsTarget, user2Token, confirmJSON);
        assertThat(confirmRequestResponse2.getStatus(), is(OK));
        final Response confirmRequestResponse3 = RequestUtil.post(friendsTarget, user3Token, confirmJSON);
        assertThat(confirmRequestResponse3.getStatus(), is(OK));

        final Response sentRequestsResponse = RequestUtil.get(sentRequestsTarget, user1Token);
        assertThat(sentRequestsResponse.getStatus(), is(OK));

        List<BasicUserData> actual = RequestUtil.parseListResponse(sentRequestsResponse, BasicUserData.class);

        assertTrue(actual.isEmpty());

        final Response receivedRequestsResponse1 = RequestUtil.get(receivedRequestsTarget, user2Token);
        assertThat(receivedRequestsResponse1.getStatus(), is(OK));

        final Response receivedRequestsResponse2 = RequestUtil.get(receivedRequestsTarget, user3Token);
        assertThat(receivedRequestsResponse2.getStatus(), is(OK));



        List<BasicUserData> actualReceived1 = RequestUtil.parseListResponse(receivedRequestsResponse1, BasicUserData.class);
        List<BasicUserData> actualReceived2 = RequestUtil.parseListResponse(receivedRequestsResponse2, BasicUserData.class);

        assertTrue(actualReceived1.isEmpty());
        assertTrue(actualReceived2.isEmpty());


        List<BasicUserData> friendsExpected1 = Arrays.asList(
                new BasicUserData(id2, username2),
                new BasicUserData(id3, username3)
        );

        Set<BasicUserData> friends1ExpectedSet = new HashSet<>(friendsExpected1);

        List<BasicUserData> user1Singleton = Collections.singletonList(new BasicUserData(id1, username1));
        Set<BasicUserData> user1Set = new HashSet<>(user1Singleton);

        final Response friendsResponse1 = RequestUtil.get(friendsTarget, user1Token);
        assertThat(friendsResponse1.getStatus(), is(OK));

        final Response friendsResponse2 = RequestUtil.get(friendsTarget, user2Token);
        assertThat(friendsResponse2.getStatus(), is(OK));

        final Response friendsResponse3 = RequestUtil.get(friendsTarget, user3Token);
        assertThat(friendsResponse3.getStatus(), is(OK));

        List<BasicUserData> actualFriends1 = RequestUtil.parseListResponse(friendsResponse1, BasicUserData.class);
        List<BasicUserData> actualFriends2 = RequestUtil.parseListResponse(friendsResponse2, BasicUserData.class);
        List<BasicUserData> actualFriends3 = RequestUtil.parseListResponse(friendsResponse3, BasicUserData.class);

        assertThat(new HashSet<>(actualFriends1), is(friends1ExpectedSet));
        assertThat(new HashSet<>(actualFriends2), is(user1Set));
        assertThat(new HashSet<>(actualFriends3), is(user1Set));
    }

    @Test
    public void anyUserGet(){
        String username1 = "username1";
        String email1 = "email1";
        String password1 = "password1";
        int id1 = addUser(username1, password1, email1);

        String username2 = "user5name2";
        String email2 = "email2";
        String password2 = "password2";
        int id2 = addUser(username2, password2, email2);

        AuthenticationToken token2 = RequestUtil.getToken(signInTarget, email2, password2);

        final WebTarget user1Target = RequestUtil.newRelativeTarget(base, String.format("users/%d", id1));
        final Response getUserResponse = RequestUtil.get(user1Target, token2);
        assertThat(getUserResponse.getStatus(), is(OK));

        final BasicUserData actualUser1Data = RequestUtil.parseResponse(getUserResponse, BasicUserData.class);
        final BasicUserData expectedUser1Data = new BasicUserData(id1, username1);

        assertThat(actualUser1Data, is(expectedUser1Data));
    }

    @Test
    public void search(){
        String username1 = "username1";
        String email1 = "email1";
        String password1 = "password1";
        int id1 = addUser(username1, password1, email1);

        String username2 = "user5name2";
        String email2 = "email2";
        String password2 = "password2";
        int id2 = addUser(username2, password2, email2);

        String username3 = "user5name3";
        String email3 = "email3";
        String password3 = "password3";
        int id3 = addUser(username3, password3, email3);



        List<BasicUserData> expectedGetAll = Arrays.asList(
                new BasicUserData(id1, username1),
                new BasicUserData(id2, username2),
                new BasicUserData(id3, username3)
        );

        final Response getAllResponse = RequestUtil.get(usersTarget, token);
        assertThat(getAllResponse.getStatus(), is(OK));

        List<BasicUserData> actualGetAll = RequestUtil.parseListResponse(getAllResponse, BasicUserData.class);

        assertContainsAll(actualGetAll, expectedGetAll);

        final String search1 = "user";

        final WebTarget search1Target = RequestUtil.newRelativeTarget(base, String.format("users?search=%s", search1));
        final Response search1Response = RequestUtil.get(search1Target, token);
        assertThat(search1Response.getStatus(), is(OK));

        List<BasicUserData> actualGetSearch1 = RequestUtil.parseListResponse(search1Response, BasicUserData.class);

        assertContainsAll(actualGetSearch1, expectedGetAll);

        final String search2 = "user5";

        List<BasicUserData> expectedSearch2 = Arrays.asList(
                new BasicUserData(id2, username2),
                new BasicUserData(id3, username3)
        );

        final WebTarget search2Target = RequestUtil.newRelativeTarget(base, String.format("users?search=%s", search2));
        final Response search2Response = RequestUtil.get(search2Target, token);
        assertThat(search2Response.getStatus(), is(OK));

        List<BasicUserData> actualGetSearch2 = RequestUtil.parseListResponse(search2Response, BasicUserData.class);

        assertContainsAll(actualGetSearch2, expectedSearch2);

        final String search3 = "user5name3";

        List<BasicUserData> expectedSearch3 = Collections.singletonList(new BasicUserData(id3, username3));

        final WebTarget search3Target = RequestUtil.newRelativeTarget(base, String.format("users?search=%s", search3));
        final Response search3Response = RequestUtil.get(search3Target, token);
        assertThat(search3Response.getStatus(), is(OK));

        List<BasicUserData> actualGetSearch3 = RequestUtil.parseListResponse(search3Response, BasicUserData.class);

        assertContainsAll(actualGetSearch3, expectedSearch3);

        final String search4 = "email3";

        List<BasicUserData> expectedSearch4 = Collections.singletonList(new BasicUserData(id3, username3));

        final WebTarget search4Target = RequestUtil.newRelativeTarget(base, String.format("users?search=%s", search4));
        final Response search4Response = RequestUtil.get(search4Target, token);
        assertThat(search4Response.getStatus(), is(OK));

        List<BasicUserData> actualGetSearch4 = RequestUtil.parseListResponse(search4Response, BasicUserData.class);

        assertContainsAll(actualGetSearch4, expectedSearch4);
    }
}