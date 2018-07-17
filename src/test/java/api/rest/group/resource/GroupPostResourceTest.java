package api.rest.group.resource;

import api.rest.ApiTest;
import api.rest.group.model.AddMemberJSON;
import api.rest.post.model.CreatePostJSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class GroupPostResourceTest extends ApiTest {

    private static final Logger logger = LogManager.getLogger(GroupPostResourceTest.class);


    @Test
    public void deletePostWhenGroupIsDeleted(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);

        final int slots = 5;
        int groupID = addGroup(slots, activityID, ownerID);

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, null, null, groupID, new HashSet<>(), new HashSet<>()));

        WebTarget groupMember1Target = RequestUtil.newRelativeTarget(
                base,
                String.format("groups/%d/members/%d", groupID, ownerID)
        );

        final Response deleteResponse = RequestUtil.delete(groupMember1Target, token);

        assertThat(deleteResponse.getStatus(), is(NO_CONTENT));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);

        final Response getDeletedPost = RequestUtil.get(postTarget, token);

        assertThat(getDeletedPost.getStatus(), is(NOT_FOUND));
    }

    @Test
    public void deletePostWhenLeaderLeaves(){
        final String gameName = "Overwatch";
        int gameID = addGame(gameName);
        final String activityName = "Ranked";
        int activityID = addActivity(activityName, gameID);
        final String ownerName = "owner";
        final String ownerPass = "123";
        final String ownerEmail = "owner@mail.com";
        int ownerID = addUser(ownerName, ownerPass, ownerEmail);
        final String username1 = "member1";
        final String password1 = "123123";
        final String email1 = "member1@lfg.com";
        int member1ID = addUser(username1, password1, email1);

        final int slots = 5;
        int groupID = addGroup(slots, activityID, ownerID);
        logger.info("groupID in test:" + groupID);

        final String description = "posty";
        final Response postResponse = RequestUtil.post(postsTarget, token, new CreatePostJSON(description, null, null, groupID, new HashSet<>(), new HashSet<>()));

        WebTarget ownerTarget = RequestUtil.newRelativeTarget(
                base,
                String.format("groups/%d/members/%d", groupID, ownerID)
        );

        WebTarget groupMembersTarget = RequestUtil.newRelativeTarget(base, String.format("groups/%d/members", groupID));

        final Response addMemberResponse = RequestUtil.post(groupMembersTarget, token, new AddMemberJSON(member1ID));
        if(addMemberResponse.getStatus() == 500){
            RequestUtil.parseResponse(addMemberResponse, String.class);
        }
        assertThat(addMemberResponse.getStatus(), is(NO_CONTENT));

        final Response deleteResponse = RequestUtil.delete(ownerTarget, token);

        assertThat(deleteResponse.getStatus(), is(NO_CONTENT));

        final String location = postResponse.getHeaderString("Location");
        final WebTarget postTarget = RequestUtil.newTarget(location);

        final Response getDeletedPost = RequestUtil.get(postTarget, token);

        assertThat(getDeletedPost.getStatus(), is(NOT_FOUND));
    }
}
