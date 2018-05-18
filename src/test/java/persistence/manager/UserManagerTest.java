package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.UserPatcher;
import persistence.entity.UserEntity;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        manager.wipe();

        final String email = "aylmaotest@mail.com";
        final String username = "testUser";
        final String password = "123123";
        UserEntity userEntity = new UserEntity(true, email, password, username);
        manager.add(userEntity);
        Optional<UserEntity> optional = manager.getByEmail(email);
        assertTrue(optional.isPresent());

        UserEntity user = optional.get();
        assertThat(user.getEmail(), is(email));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(password));

        manager.wipe();
    }

    @Test
    public void changePass(){
        manager.wipe();

        String email = "aylmaotest@mail.com";
        String pass = "123123";
        UserEntity userEntity = new UserEntity(true, email, pass, "testUser");
        manager.add(userEntity);

        String newPass = "123456";

        Optional<UserEntity> optional = manager.getByEmail(email);
        UserEntity user = optional.orElseThrow(() -> new RuntimeException("No user"));
        assertThat(user.getPassword(), is(pass));
        assertThat(user.getPassword(), is(not(newPass)));


        UserPatcher patcher = new UserPatcher.Builder()
                .withPassword(newPass)
                .build();

        manager.updateUser(user.getId(), patcher);

        optional = manager.getByEmail(email);
        user = optional.orElseThrow(() -> new RuntimeException("No user"));

        assertThat(user.getPassword(), is(not(pass)));
        assertThat(user.getPassword(), is(newPass));

        manager.wipe();
    }

    @Test
    public void listUsers(){
        manager.wipe();

        UserDetails one = new UserDetails("Username", "pass", "email@email.com", false);
        UserDetails two = new UserDetails("someone", "pass", "another@email.com", false);
        UserDetails three = new UserDetails("HeyImAdmin", "pass", "test@admin.com", true);
        UserDetails[] expected = addAll(one, two, three);

        List<UserDetails> actual = manager.list().stream()
                .map(manager::get)
                .map(UserDetails::new)
                .collect(Collectors.toList());

        assertThat(actual, hasItems(expected));

        manager.wipe();
    }

    @Test
    public void duplicateUserExc(){
        manager.wipe();

        UserDetails one = new UserDetails("Username", "pass", "email@email.com", false);

        UserEntity userEntity = new UserEntity(one.admin, one.email, one.password, one.username);
        manager.add(userEntity);

        assertTrue(manager.userExistsByEmail(one.email));
        assertTrue(manager.userExistsByUserName(one.username));

        try {
            UserEntity userEntity2 = new UserEntity(one.admin, "Another email", one.password, one.username);
            manager.add(userEntity2);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(one.username));
        }

        try {
            UserEntity userEntity3 = new UserEntity(one.admin, one.email, one.password, "Another username");
            manager.add(userEntity3);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(one.email));
        }

        manager.wipe();
    }

    @Test
    public void duplicateUserExcUpdate() {
        manager.wipe();

        UserDetails one = new UserDetails("Username", "pass", "email@email.com", false);
        UserDetails two = new UserDetails("someone", "pass", "another@email.com", false);
        UserDetails three = new UserDetails("HeyImAdmin", "pass", "test@admin.com", true);
        UserDetails[] expected = addAll(one, two, three);

        UserEntity user = manager.getByEmail(one.email).get();

        UserPatcher emailPatcher = new UserPatcher.Builder().withEmail(two.email).build();
        UserPatcher usernamePatcher = new UserPatcher.Builder().withUsername(two.username).build();

        try {
            manager.updateUser(user.getId(), emailPatcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(two.email));
        }

        try {
            manager.updateUser(user.getId(), usernamePatcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(two.username));
        }


        manager.wipe();
    }

    private UserDetails[] addAll(UserDetails... details){
        Arrays.stream(details).map(x -> new UserEntity(x.admin, x.email, x.password, x.username))
                .forEach(y -> manager.add(y));
        return details;
    }

    private class UserDetails{
        String username;
        String password;
        String email;
        boolean admin;

        public UserDetails(String username, String password, String email, boolean admin) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.admin = admin;
        }

        public UserDetails(UserEntity user){
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.email = user.getEmail();
            this.admin = user.isAdmin();
        }

        @Override
        public String toString() {
            return "UserDetails{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +
                    ", admin=" + admin +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserDetails)) return false;
            UserDetails details = (UserDetails) o;
            return admin == details.admin &&
                    Objects.equals(username, details.username) &&
                    Objects.equals(password, details.password) &&
                    Objects.equals(email, details.email);
        }

        @Override
        public int hashCode() {

            return Objects.hash(username, password, email, admin);
        }
    }
}
