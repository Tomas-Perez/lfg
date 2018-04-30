package restapi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import persistence.manager.*;
import restapi.activity.model.CreateActivityJSON;
import restapi.game.model.CreateGameJSON;
import restapi.security.authentication.model.AuthenticationToken;
import util.ManagerUtil;
import util.RequestUtil;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Tomas Perez Molina
 */
public abstract class ApiTest {
    protected EntityManagerProducer emp;
    protected UserManager userManager;
    protected AuthenticationToken token;
    protected GameManager gameManager;
    protected ActivityManager activityManager;
    protected GroupManager groupManager;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @ArquillianResource
    protected URL base;

    @Before
    public void setup() throws Exception{
        emp = new EntityManagerProducer();
        emp.init();
        groupManager = new GroupManager(emp.createEntityManager());
        groupManager.wipeAllRecords();
        userManager = new UserManager(emp.createEntityManager());
        userManager.wipeAllRecords();
        activityManager = new ActivityManager(emp.createEntityManager());
        activityManager.wipeAllRecords();
        gameManager = new GameManager(emp.createEntityManager());
        gameManager.wipeAllRecords();

        String email = "aylmaotest@mail.com";
        String password = "123123";
        ManagerUtil.addUser(
                userManager,
                "testUser",
                password,
                email,
                true
        );

        token = RequestUtil.getToken(base, email, password);

        assertNotNull(groupManager);
        assertNotNull(userManager);
        assertNotNull(activityManager);
        assertNotNull(gameManager);
    }

    @After
    public void cleanUp(){
        groupManager = new GroupManager(emp.createEntityManager());
        groupManager.wipeAllRecords();
        activityManager = new ActivityManager(emp.createEntityManager());
        activityManager.wipeAllRecords();
        gameManager = new GameManager(emp.createEntityManager());
        gameManager.wipeAllRecords();
        userManager = new UserManager(emp.createEntityManager());
        userManager.wipeAllRecords();
        emp.destroy();
    }

    protected int addGame(WebTarget webTarget, String name){
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateGameJSON(name));
        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(webTarget, gameTarget));
    }

    protected int addActivity(WebTarget webTarget, String name, int gameID){
        final Response postResponse = RequestUtil.post(webTarget, token, new CreateActivityJSON(name, gameID));
        assertThat(postResponse.getStatus(), is(Response.Status.CREATED.getStatusCode()));
        final String location = postResponse.getHeaderString("Location");
        final WebTarget gameTarget = RequestUtil.newTarget(location);
        return Integer.parseInt(RequestUtil.getRelativePathDiff(webTarget, gameTarget));
    }
}
