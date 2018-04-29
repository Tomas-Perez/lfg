package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.model.*;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class PostManagerTest {
    @Inject
    private UserManager userManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private GameManager gameManager;

    @Inject
    private GroupManager groupManager;

    @Inject
    private PostManager postManager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Test
    public void shouldCreateManager() {
        assertNotNull(postManager);
        assertNotNull(groupManager);
        assertNotNull(activityManager);
        assertNotNull(gameManager);
        assertNotNull(userManager);
    }

    @Test
    public void addPost(){
        cleanUp();

        List<Post> posts = postManager.listPosts();

        assertThat(posts.size(), is(0));

        final String gameName = "Overwatch";
        Game ow = addGame(gameName);

        final String activityName = "Ranked";
        Activity ranked = addActivity(activityName, ow);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);

        final LocalDateTime dateTime = LocalDateTime.now();

        final String description = "whatever";
        postManager.addPost(description, dateTime, ranked, user);

        posts = postManager.listPosts();

        assertThat(posts.size(), is(1));
        Post post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivity(), is(ranked));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwner(), is(user));

        cleanUp();
    }

    @Test
    public void addGroupPost(){
        cleanUp();

        List<Post> posts = postManager.listPosts();

        assertThat(posts.size(), is(0));

        final String gameName = "Overwatch";
        Game ow = addGame(gameName);

        final String activityName = "Ranked";
        Activity ranked = addActivity(activityName, ow);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);


        Group group = addGroup(5, ranked, user);
        final LocalDateTime dateTime = LocalDateTime.now();

        final String description = "whatever";
        postManager.addGroupPost(description, dateTime, group);

        posts = postManager.listPosts();

        assertThat(posts.size(), is(1));
        Post post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivity(), is(ranked));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwner(), is(user));
        assertThat(post.getGroup(), is(group));

        cleanUp();
    }

    private void cleanUp(){
        postManager.wipeAllRecords();
        groupManager.wipeAllRecords();
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
        userManager.wipeAllRecords();
    }

    private Game addGame(String name){
        gameManager.addGame(name, null);
        Optional<Game> optional = gameManager.getByName(name);
        return optional.orElseThrow(() -> new RuntimeException("Game not saved"));
    }

    private Activity addActivity(String name, Game game){
        activityManager.addActivity(name, game);
        Optional<Activity> optional = activityManager.getActivity(name, game);
        return optional.orElseThrow(() -> new RuntimeException("Activity not saved"));
    }

    private User addUser(String username, String password, String email, boolean admin){
        userManager.addUser(username, password, email, admin);
        Optional<User> optional = userManager.getByEmail(email);
        return optional.orElseThrow(() -> new RuntimeException("User not saved"));
    }

    private Group addGroup(int slots, Activity activity, User owner){
        int id = groupManager.addGroup(slots, activity, owner, null, null);
        return groupManager.getGroup(id);
    }
}