package persistence.manager;

import model.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.*;
import persistence.model.*;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        List<PostEntity> posts = postManager.listPosts()
                .stream()
                .map(postManager::getPost)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(0));

        final String gameName = "Overwatch";
        GameEntity ow = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity ranked = addActivity(activityName, ow);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);

        final LocalDateTime dateTime = LocalDateTime.now();

        final String description = "whatever";
        postManager.addPost(description, dateTime, ranked.getId(), user.getId());

        posts = postManager.listPosts().stream()
                .map(postManager::getPost)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(1));
        PostEntity post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivityId(), is(ranked.getId()));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwnerId(), is(user.getId()));

        cleanUp();
    }

    @Test
    public void addGroupPost(){
        cleanUp();

        List<PostEntity> posts = postManager.listPosts()
                .stream()
                .map(postManager::getPost)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(0));

        final String gameName = "Overwatch";
        GameEntity ow = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity ranked = addActivity(activityName, ow);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);


        GroupEntity group = addGroup(5, ranked, user);
        final LocalDateTime dateTime = LocalDateTime.now();

        final String description = "whatever";
        postManager.addGroupPost(description, dateTime, group);

        posts = postManager.listPosts()
                .stream()
                .map(postManager::getPost)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(1));
        PostEntity post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivityId(), is(ranked.getId()));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwnerId(), is(user.getId()));
        assertThat(post.getGroupId(), is(group.getId()));

        cleanUp();
    }

    private void cleanUp(){
        postManager.wipeAllRecords();
        groupManager.wipeAllRecords();
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
        userManager.wipeAllRecords();
    }

    private GameEntity addGame(String name){
        gameManager.addGame(name, null);
        Optional<GameEntity> optional = gameManager.getByName(name);
        return optional.orElseThrow(() -> new RuntimeException("GameEntity not saved"));
    }

    private ActivityEntity addActivity(String name, GameEntity game){
        activityManager.addActivity(name, game.getId());
        Optional<ActivityEntity> optional = activityManager.getActivity(name, game.getId());
        return optional.orElseThrow(() -> new RuntimeException("ActivityEntity not saved"));
    }

    private UserEntity addUser(String username, String password, String email, boolean admin){
        userManager.addUser(username, password, email, admin);
        Optional<UserEntity> optional = userManager.getByEmail(email);
        return optional.orElseThrow(() -> new RuntimeException("UserEntity not saved"));
    }

    private GroupEntity addGroup(int slots, ActivityEntity activity, UserEntity owner){
        int id = groupManager.addGroup(slots, activity.getId(), owner.getId(), null, null);
        return groupManager.getGroup(id);
    }
}