package api.rest.security.authentication.resource;

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
import persistence.entity.UserEntity;
import persistence.manager.EntityManagerProducer;
import persistence.manager.FriendHelperManager;
import persistence.manager.UserManager;
import api.rest.security.authentication.model.SignInJSON;

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

        manager = new UserManager(emp.createEntityManager(), new FriendHelperManager());
    }

    @After
    public void cleanUp(){
        emp.destroy();
    }

    @Test
    public void authenticateUser(
            @ArquillianResteasyResource("sign-in") final WebTarget webTarget)
    {
        manager.wipe();

        String email = "aylmaotest@mail.com";
        String password = "123123";
        UserEntity userEntity = new UserEntity(true, email, password, "testUser");
        manager.add(userEntity);

        final Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new SignInJSON(
                        email,
                        password)));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        manager.wipe();
    }
}
