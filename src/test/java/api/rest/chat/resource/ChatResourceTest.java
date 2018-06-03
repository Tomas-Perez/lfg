package api.rest.chat.resource;

import api.rest.ApiTest;
import api.rest.chat.model.AddMemberJSON;
import api.rest.chat.model.ChatJSON;
import api.rest.chat.model.CreateChatJSON;
import api.rest.chat.model.WebsocketPathJSON;
import api.rest.group.model.MemberJSON;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class ChatResourceTest extends ApiTest {

    @Test
    public void create(){
        final String username1 = "user1";
        final String password1 = "123123";
        final String email1 = "email1";
        int user1ID = addUser(username1, password1, email1);

        final String username2 = "user2";
        final String password2 = "123123";
        final String email2 = "email2";
        int user2ID = addUser(username2, password2, email2);

        final String username3 = "user3";
        final String password3 = "123123";
        final String email3 = "email3";
        int user3ID = addUser(username3, password3, email3);

        List<Integer> members = Arrays.asList(user1ID, user2ID, user3ID);

        final Response postResponse = RequestUtil.post(chatsTarget, token, new CreateChatJSON(CreateChatJSON.ChatType.GROUP, members));

        assertThat(postResponse.getStatus(), is(CREATED));

        WebsocketPathJSON path = RequestUtil.parseResponse(postResponse, WebsocketPathJSON.class);

        System.out.println(System.lineSeparator());
        System.out.println(path);
        System.out.println(System.lineSeparator());

        final String location = postResponse.getHeaderString("Location");
        final WebTarget chatTarget = RequestUtil.newTarget(location);
        final Integer id = Integer.parseInt(RequestUtil.getRelativePathDiff(chatsTarget, chatTarget));

        final Response getResponse = RequestUtil.get(chatTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        ChatJSON actual = RequestUtil.parseResponse(getResponse, ChatJSON.class);

        List<MemberJSON> memberJSONS = Arrays.asList(
                new MemberJSON(user1ID, username1),
                new MemberJSON(user2ID, username2),
                new MemberJSON(user3ID, username3)
        );

        ChatJSON expected = new ChatJSON(id, memberJSONS, Collections.emptyList());

        assertThat(actual, is(expected));
    }

    @Test
    public void notFoundGet(@ArquillianResteasyResource("chats/1") final WebTarget webTarget){
        final Response response = RequestUtil.get(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void notFoundDelete(@ArquillianResteasyResource("chats/1") final WebTarget webTarget){
        final Response response = RequestUtil.delete(webTarget, token);
        assertThat(response.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void addRemoveMembers(){
        final String username1 = "user1";
        final String password1 = "123123";
        final String email1 = "email1";
        int user1ID = addUser(username1, password1, email1);

        final String username2 = "user2";
        final String password2 = "123123";
        final String email2 = "email2";
        int user2ID = addUser(username2, password2, email2);

        final String username3 = "user3";
        final String password3 = "123123";
        final String email3 = "email3";
        int user3ID = addUser(username3, password3, email3);

        List<Integer> startMembers = Arrays.asList(user1ID, user2ID, user3ID);

        int chatID = addChat(CreateChatJSON.ChatType.GROUP, startMembers);

        WebTarget chatMembersTarget = RequestUtil.newRelativeTarget(base, String.format("chats/%d/members", chatID));

        final String username4 = "user4";
        final String password4 = "123123";
        final String email4 = "email4";
        int user4ID = addUser(username4, password4, email4);

        final String username5 = "user5";
        final String password5 = "123123";
        final String email5 = "email5";
        int user5ID = addUser(username5, password5, email5);

        final Response postResponse = RequestUtil.post(chatMembersTarget, token, new AddMemberJSON(user4ID));

        assertThat(postResponse.getStatus(), is(NO_CONTENT));

        final Response postResponse2 = RequestUtil.post(chatMembersTarget, token, new AddMemberJSON(user5ID));

        assertThat(postResponse2.getStatus(), is(NO_CONTENT));


        WebTarget chatTarget = RequestUtil.newRelativeTarget(base, String.format("chats/%d", chatID));

        final Response getResponse = RequestUtil.get(chatTarget, token);

        assertThat(getResponse.getStatus(), is(OK));

        ChatJSON actual = RequestUtil.parseResponse(getResponse, ChatJSON.class);

        final MemberJSON member1JSON = new MemberJSON(user1ID, username1);
        final MemberJSON member2JSON = new MemberJSON(user2ID, username2);
        final MemberJSON member3JSON = new MemberJSON(user3ID, username3);
        final MemberJSON member4JSON = new MemberJSON(user4ID, username4);
        final MemberJSON member5JSON = new MemberJSON(user5ID, username5);

        List<MemberJSON> expectedMembers = Arrays.asList(
                member1JSON,
                member2JSON,
                member3JSON,
                member4JSON,
                member5JSON
        );


        ChatJSON expected = new ChatJSON(chatID, expectedMembers, Collections.emptyList());

        assertThat(actual, is(expected));

        WebTarget chatMember1Target = RequestUtil.newRelativeTarget(
                base,
                String.format("chats/%d/members/%d", chatID, user1ID)
        );

        final Response deleteResponse = RequestUtil.delete(chatMember1Target, token);

        System.out.println(deleteResponse.readEntity(String.class));

        assertThat(deleteResponse.getStatus(), is(NO_CONTENT));

        final Response getResponse2 = RequestUtil.get(chatTarget, token);

        assertThat(getResponse2.getStatus(), is(OK));

        actual = RequestUtil.parseResponse(getResponse2, ChatJSON.class);

        List<MemberJSON> expectedMembers2 = Arrays.asList(
                member2JSON,
                member3JSON,
                member4JSON,
                member5JSON
        );

        expected.setMembers(expectedMembers2);

        assertThat(actual, is(expected));
    }
}