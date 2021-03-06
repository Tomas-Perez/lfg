package api.rest;

import api.rest.chat.model.CreateChatJSON;
import api.rest.chatPlatform.model.CreateChatPlatformJSON;
import api.rest.gamePlatform.model.CreateGamePlatformJSON;
import api.rest.post.model.CreatePostJSON;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import persistence.entity.UserEntity;
import persistence.manager.*;
import api.rest.activity.model.CreateActivityJSON;
import api.rest.game.model.CreateGameJSON;
import api.rest.group.model.CreateGroupJSON;
import api.rest.security.authentication.model.AuthenticationToken;
import api.rest.security.authentication.model.SignInJSON;
import api.rest.signup.model.SignUpJSON;
import api.rest.user.model.UserData;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Tomas Perez Molina
 */
public abstract class ApiTest {

    private EntityManagerProducer emp;
    private UserManager userManager;
    private GameManager gameManager;
    private ActivityManager activityManager;
    private GroupManager groupManager;
    private PostManager postManager;
    private ChatManager chatManager;
    private ChatPlatformManager chatPlatformManager;
    private GamePlatformManager gamePlatformManager;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @ArquillianResource
    protected URL base;

    protected AuthenticationToken token;

    protected WebTarget gamesTarget;
    protected WebTarget activitiesTarget;
    protected WebTarget groupsTarget;
    protected WebTarget postsTarget;
    protected WebTarget signInTarget;
    protected WebTarget signUpTarget;
    protected WebTarget usersTarget;
    protected WebTarget meTarget;
    protected WebTarget chatsTarget;
    protected WebTarget chatPlatformsTarget;
    protected WebTarget gamePlatformsTarget;

    private static final String USERNAME = "testUser";
    private static final String EMAIL = "test@mail.com";
    private static final String PASSWORD = "123123";

    protected static final int OK = Response.Status.OK.getStatusCode();
    protected static final int NO_CONTENT = Response.Status.NO_CONTENT.getStatusCode();
    protected static final int CONFLICT = Response.Status.CONFLICT.getStatusCode();
    protected static final int NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();
    protected static final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();
    protected static final int CREATED = Response.Status.CREATED.getStatusCode();

    @Before
    public void setup(){
        emp = new EntityManagerProducer();
        emp.init();
        gameManager = new GameManager(emp.createEntityManager());
        gameManager.wipe();
        userManager = new UserManager(emp.createEntityManager(), new FriendHelperManager());
        userManager.wipe();
        activityManager = new ActivityManager(emp.createEntityManager(), gameManager);
        activityManager.wipe();
        gamePlatformManager = new GamePlatformManager(emp.createEntityManager());
        gamePlatformManager.wipe();
        chatPlatformManager = new ChatPlatformManager(emp.createEntityManager());
        chatPlatformManager.wipe();
        groupManager = new GroupManager(emp.createEntityManager(), userManager, activityManager, chatPlatformManager, gamePlatformManager);
        groupManager.wipe();
        postManager = new PostManager(emp.createEntityManager(), userManager, activityManager, groupManager, chatPlatformManager, gamePlatformManager);
        postManager.wipe();
        chatManager = new ChatManager(emp.createEntityManager(), userManager, groupManager);
        chatManager.wipe();


        UserEntity userEntity = new UserEntity(true, EMAIL, PASSWORD, USERNAME);
        userManager.add(userEntity);

        assertNotNull(groupManager);
        assertNotNull(userManager);
        assertNotNull(activityManager);
        assertNotNull(gameManager);

        gamesTarget = RequestUtil.newRelativeTarget(base, "games");
        activitiesTarget = RequestUtil.newRelativeTarget(base, "activities");
        groupsTarget = RequestUtil.newRelativeTarget(base, "groups");
        postsTarget = RequestUtil.newRelativeTarget(base, "posts");
        signInTarget = RequestUtil.newRelativeTarget(base, "sign-in");
        signUpTarget = RequestUtil.newRelativeTarget(base, "sign-up");
        usersTarget = RequestUtil.newRelativeTarget(base, "users");
        meTarget = RequestUtil.newRelativeTarget(base, "users/me");
        chatsTarget = RequestUtil.newRelativeTarget(base, "chats");
        chatPlatformsTarget = RequestUtil.newRelativeTarget(base, "chat-platforms");
        gamePlatformsTarget = RequestUtil.newRelativeTarget(base, "game-platforms");

        token = RequestUtil.getToken(signInTarget, EMAIL, PASSWORD);
    }

    @After
    public void cleanUp(){
        gameManager = new GameManager(emp.createEntityManager());
        gameManager.wipe();
        userManager = new UserManager(emp.createEntityManager(), new FriendHelperManager());
        userManager.wipe();
        activityManager = new ActivityManager(emp.createEntityManager(), gameManager);
        activityManager.wipe();
        gamePlatformManager = new GamePlatformManager(emp.createEntityManager());
        gamePlatformManager.wipe();
        chatPlatformManager = new ChatPlatformManager(emp.createEntityManager());
        chatPlatformManager.wipe();
        groupManager = new GroupManager(emp.createEntityManager(), userManager, activityManager, chatPlatformManager, gamePlatformManager);
        groupManager.wipe();
        postManager = new PostManager(emp.createEntityManager(), userManager, activityManager, groupManager, chatPlatformManager, gamePlatformManager);
        postManager.wipe();
        chatManager = new ChatManager(emp.createEntityManager(), userManager, groupManager);
        chatManager.wipe();
        emp.destroy();
    }

    protected int addGame(String name){
        final Response postResponse = RequestUtil.post(gamesTarget, token, new CreateGameJSON(name));
        assertThat(postResponse.getStatus(), is(CREATED));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(gamesTarget, gameTarget));
    }

    protected int addChatPlatform(String name){
        final CreateChatPlatformJSON entity = new CreateChatPlatformJSON(name);
        return addJSON(chatPlatformsTarget, entity);
    }

    protected int addGamePlatform(String name){
        final CreateGamePlatformJSON entity = new CreateGamePlatformJSON(name);
        return addJSON(gamePlatformsTarget, entity);
    }

    protected int addActivity(String name, int gameID){
        final Response postResponse = RequestUtil.post(activitiesTarget, token, new CreateActivityJSON(name, gameID));
        assertThat(postResponse.getStatus(), is(CREATED));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget activityTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(activitiesTarget, activityTarget));
    }

    protected int addGroup(int slots, int activityID, int userID){
        final Response postResponse = RequestUtil.post(groupsTarget, token, new CreateGroupJSON(slots, activityID, userID, null, null));
        assertThat(postResponse.getStatus(), is(CREATED));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget groupTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(groupsTarget, groupTarget));
    }

    protected int addChat(CreateChatJSON chatJSON){
        return addJSON(chatsTarget, chatJSON);
    }

    protected int addPost(CreatePostJSON postJSON){
        return addJSON(postsTarget, postJSON);
    }

    protected int addGroup(CreateGroupJSON groupJSON){
        return addJSON(groupsTarget, groupJSON);
    }

    protected <T> int addJSON(WebTarget target, T json){
        final Response postResponse = RequestUtil.post(target, token, json);
        System.out.println(postResponse.readEntity(String.class));
        assertThat(postResponse.getStatus(), is(CREATED));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget jsonTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(target, jsonTarget));
    }

    protected int addUser(String username, String password, String email){
        if(USERNAME.equals(username)){
            throw new RuntimeException("Username cannot be " + USERNAME);
        }
        if(EMAIL.equals(email)){
            throw new RuntimeException("Email cannot be " + EMAIL);
        }

        final Response signUpResponse = RequestUtil.post(signUpTarget, token, new SignUpJSON(email, password, username));
        assertThat(signUpResponse.getStatus(), is(CREATED));

        final Response signInResponse = RequestUtil.post(signInTarget, token, new SignInJSON(email, password));
        assertThat(signInResponse.getStatus(), is(OK));

        AuthenticationToken userToken;
        try {
            userToken = RequestUtil.parseResponse(signInResponse, AuthenticationToken.class);
        } catch (Exception e) {
            System.out.println(signInResponse.readEntity(String.class));
            throw new RuntimeException(e);
        }
        final Response getUserResponse = RequestUtil.get(meTarget, userToken);


        UserData userData;
        try {
            userData = RequestUtil.parseResponse(getUserResponse, UserData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userData.getId();
    }

    protected <T> void assertContainsAll(List<T> actual, List<T> expected){
        expected.forEach(x -> assertTrue(actual.contains(x)));
    }
}
