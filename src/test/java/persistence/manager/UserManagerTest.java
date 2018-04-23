package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.model.User;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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

    @Test
    public void changePass(){
        String email = "aylmaotest@mail.com";
        String pass = "123123";
        manager.addUser(
                "testUser",
                pass,
                email,
                true
        );

        String newPass = "123456";

        Optional<User> optional = manager.getByEmail(email);
        User user = optional.orElseThrow(() -> new RuntimeException("No user"));
        assertThat(user.getPassword(), is(pass));
        assertThat(user.getPassword(), is(not(newPass)));


        manager.updateUser(user.getUserId(), newPass);

        optional = manager.getByEmail(email);
        user = optional.orElseThrow(() -> new RuntimeException("No user"));

        assertThat(user.getPassword(), is(not(pass)));
        assertThat(user.getPassword(), is(newPass));

        manager.deleteUser(user.getUserId());
    }

}
