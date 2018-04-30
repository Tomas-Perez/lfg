package restapi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import persistence.manager.*;
import restapi.security.authentication.model.AuthenticationToken;
import util.ManagerUtil;
import util.RequestUtil;

import java.net.URL;

import static org.junit.Assert.assertNotNull;

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
    private URL base;

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
}
