package restapi.security.authentication.resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.EntityManagerProducer;
import persistence.manager.UserManager;
import restapi.security.authentication.model.SignInJSON;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class SignInResourceTest {

    private UserManager manager;
    private EntityManagerProducer emp;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Before
    public void setup(){
        emp = new EntityManagerProducer();
        emp.init();
        manager = new UserManager(emp.createEntityManager());
    }

    @After
    public void cleanUp(){
        emp.destroy();
    }

    @Test
    public void authenticateUser(
            @ArquillianResteasyResource("sign-in") final WebTarget webTarget)
    {
        manager.wipeAllRecords();

        String email = "aylmaotest@mail.com";
        String password = "123123";
        manager.addUser(
                "testUser",
                password,
                email,
                true
        );

        final Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new SignInJSON(
                        email,
                        password)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        manager.wipeAllRecords();
    }
}
