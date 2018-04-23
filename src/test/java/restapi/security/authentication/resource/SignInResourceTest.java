package restapi.security.authentication.resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import restapi.security.authentication.model.SignInJSON;

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
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Test
    public void authenticateUser(
            @ArquillianResteasyResource("sign-in") final WebTarget webTarget)
    {
        final Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new SignInJSON(
                        "wewey@lfg.com",
                        "123123")));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
