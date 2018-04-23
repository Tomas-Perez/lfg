package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.model.User;

import javax.inject.Inject;

import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class UserManagerTest {
    @Inject
    private UserManager manager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }



    @Test
    public void shouldCreateManager() {
        assertNotNull(manager);
    }

    @Test
    public void insertUser(){
        String email = "aylmaotest@mail.com";
        manager.addUser(
                "testUser",
                "123123",
                email,
                true
        );
        Optional<User> optional = manager.getByEmail(email);
        assertTrue(optional.isPresent());
        manager.deleteUser(optional.get().getUserId());
    }

}
