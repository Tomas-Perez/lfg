package restapi.user.resource;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.ApiTest;
import restapi.group.model.MemberJSON;
import restapi.security.authentication.model.AuthenticationToken;
import restapi.user.model.ConfirmRequestJSON;
import restapi.user.model.FriendRequestJSON;
import restapi.user.model.UserData;
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
    public void getMe() throws Exception{
        String username = "username";
        String email = "email";
        String password = "password";
        int id = addUser(username, password, email);

        final AuthenticationToken userToken = RequestUtil.getToken(base, email, password);

        final Response meResponse = RequestUtil.get(meTarget, userToken);
        UserData meData = RequestUtil.parseResponse(meResponse, UserData.class);

        UserData expected = new UserData(id, username, email, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertThat(meData, is(expected));
    }

    @Test
    public void sendFriendRequests() throws Exception{
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

        final AuthenticationToken user1Token = RequestUtil.getToken(base, email1, password1);
        final AuthenticationToken user2Token = RequestUtil.getToken(base, email2, password2);
        final AuthenticationToken user3Token = RequestUtil.getToken(base, email3, password3);

        final WebTarget requestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests");
        final WebTarget sentRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/sent");
        final WebTarget receivedRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/received");

        final Response sendRequestResponse1 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id2));
        assertThat(sendRequestResponse1.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final Response sendRequestResponse2 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id3));
        assertThat(sendRequestResponse2.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        List<MemberJSON> expected = Arrays.asList(
                new MemberJSON(id2, username2),
                new MemberJSON(id3, username3)
        );

        final Response sentRequestsResponse = RequestUtil.get(sentRequestsTarget, user1Token);
        assertThat(sentRequestsResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<MemberJSON> actual = RequestUtil.parseListResponse(sentRequestsResponse, MemberJSON.class);

        assertThat(new HashSet<>(actual), is(new HashSet<>(expected)));

        final Response receivedRequestsResponse1 = RequestUtil.get(receivedRequestsTarget, user2Token);
        assertThat(receivedRequestsResponse1.getStatus(), is(Response.Status.OK.getStatusCode()));

        final Response receivedRequestsResponse2 = RequestUtil.get(receivedRequestsTarget, user3Token);
        assertThat(receivedRequestsResponse2.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<MemberJSON> user1Singleton = Collections.singletonList(new MemberJSON(id1, username1));
        Set<MemberJSON> user1Set = new HashSet<>(user1Singleton);

        List<MemberJSON> actualReceived1 = RequestUtil.parseListResponse(receivedRequestsResponse1, MemberJSON.class);
        List<MemberJSON> actualReceived2 = RequestUtil.parseListResponse(receivedRequestsResponse2, MemberJSON.class);

        assertThat(new HashSet<>(actualReceived1), is(user1Set));
        assertThat(new HashSet<>(actualReceived2), is(user1Set));
    }

    @Test
    public void confirmFriendRequest() throws Exception{
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

        final AuthenticationToken user1Token = RequestUtil.getToken(base, email1, password1);
        final AuthenticationToken user2Token = RequestUtil.getToken(base, email2, password2);
        final AuthenticationToken user3Token = RequestUtil.getToken(base, email3, password3);

        final WebTarget requestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests");
        final WebTarget sentRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/sent");
        final WebTarget receivedRequestsTarget = RequestUtil.newRelativeTarget(base, "users/me/friend-requests/received");

        final Response sendRequestResponse1 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id2));
        assertThat(sendRequestResponse1.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final Response sendRequestResponse2 = RequestUtil.post(requestsTarget, user1Token, new FriendRequestJSON(id3));
        assertThat(sendRequestResponse2.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final WebTarget friendsTarget = RequestUtil.newRelativeTarget(base, "users/me/friends");

        final ConfirmRequestJSON confirmJSON = new ConfirmRequestJSON(id1);
        final Response confirmRequestResponse2 = RequestUtil.post(friendsTarget, user2Token, confirmJSON);
        assertThat(confirmRequestResponse2.getStatus(), is(Response.Status.OK.getStatusCode()));
        final Response confirmRequestResponse3 = RequestUtil.post(friendsTarget, user3Token, confirmJSON);
        assertThat(confirmRequestResponse3.getStatus(), is(Response.Status.OK.getStatusCode()));

        final Response sentRequestsResponse = RequestUtil.get(sentRequestsTarget, user1Token);
        assertThat(sentRequestsResponse.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<MemberJSON> actual = RequestUtil.parseListResponse(sentRequestsResponse, MemberJSON.class);

        assertTrue(actual.isEmpty());

        final Response receivedRequestsResponse1 = RequestUtil.get(receivedRequestsTarget, user2Token);
        assertThat(receivedRequestsResponse1.getStatus(), is(Response.Status.OK.getStatusCode()));

        final Response receivedRequestsResponse2 = RequestUtil.get(receivedRequestsTarget, user3Token);
        assertThat(receivedRequestsResponse2.getStatus(), is(Response.Status.OK.getStatusCode()));



        List<MemberJSON> actualReceived1 = RequestUtil.parseListResponse(receivedRequestsResponse1, MemberJSON.class);
        List<MemberJSON> actualReceived2 = RequestUtil.parseListResponse(receivedRequestsResponse2, MemberJSON.class);

        assertTrue(actualReceived1.isEmpty());
        assertTrue(actualReceived2.isEmpty());


        List<MemberJSON> friendsExpected1 = Arrays.asList(
                new MemberJSON(id2, username2),
                new MemberJSON(id3, username3)
        );

        Set<MemberJSON> friends1ExpectedSet = new HashSet<>(friendsExpected1);

        List<MemberJSON> user1Singleton = Collections.singletonList(new MemberJSON(id1, username1));
        Set<MemberJSON> user1Set = new HashSet<>(user1Singleton);




        final Response friendsResponse1 = RequestUtil.get(friendsTarget, user1Token);
        assertThat(friendsResponse1.getStatus(), is(Response.Status.OK.getStatusCode()));

        final Response friendsResponse2 = RequestUtil.get(friendsTarget, user2Token);
        assertThat(friendsResponse2.getStatus(), is(Response.Status.OK.getStatusCode()));

        final Response friendsResponse3 = RequestUtil.get(friendsTarget, user3Token);
        assertThat(friendsResponse3.getStatus(), is(Response.Status.OK.getStatusCode()));

        List<MemberJSON> actualFriends1 = RequestUtil.parseListResponse(friendsResponse1, MemberJSON.class);
        List<MemberJSON> actualFriends2 = RequestUtil.parseListResponse(friendsResponse2, MemberJSON.class);
        List<MemberJSON> actualFriends3 = RequestUtil.parseListResponse(friendsResponse3, MemberJSON.class);

        assertThat(new HashSet<>(actualFriends1), is(friends1ExpectedSet));
        assertThat(new HashSet<>(actualFriends2), is(user1Set));
        assertThat(new HashSet<>(actualFriends3), is(user1Set));
    }
}