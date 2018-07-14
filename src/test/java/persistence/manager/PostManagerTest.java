package persistence.manager;

import org.junit.After;
import org.junit.Before;
import persistence.entity.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Before
    @After
    public void cleanUp(){
        postManager.wipe();
        groupManager.wipe();
        activityManager.wipe();
        gameManager.wipe();
        userManager.wipe();
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
        List<PostEntity> posts = postManager.list()
                .stream()
                .map(postManager::get)
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
        PostEntity postEntity = new PostEntity(description, dateTime, ranked.getId(), user.getId(), null);
        postManager.add(postEntity);

        posts = postManager.list().stream()
                .map(postManager::get)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(1));
        PostEntity post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivityId(), is(ranked.getId()));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwnerId(), is(user.getId()));
    }

    @Test
    public void addGroupPost(){
        List<PostEntity> posts = postManager.list()
                .stream()
                .map(postManager::get)
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
        postManager.addGroupPost(description, dateTime, group, new HashSet<>(), new HashSet<>());

        posts = postManager.list()
                .stream()
                .map(postManager::get)
                .collect(Collectors.toList());

        assertThat(posts.size(), is(1));
        PostEntity post = posts.get(0);

        assertThat(post.getDescription(), is(description));
        assertThat(post.getActivityId(), is(ranked.getId()));
        assertThat(post.getDate(), is(dateTime));
        assertThat(post.getOwnerId(), is(user.getId()));
        assertThat(post.getGroupId(), is(group.getId()));
    }

    @Test
    public void getUserPost(){
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
        PostEntity postEntity = new PostEntity(description, dateTime, ranked.getId(), user.getId(), null);
        int expectedID = postManager.add(postEntity);

        final Integer actualID = userManager.getUserPost(user.getId());
        assertThat(actualID, is(expectedID));
    }


    private GameEntity addGame(String name){
        GameEntity gameEntity = new GameEntity(null, name);
        gameManager.add(gameEntity);
        Optional<GameEntity> optional = gameManager.getByName(name);
        return optional.orElseThrow(() -> new RuntimeException("GameEntity not saved"));
    }

    private ActivityEntity addActivity(String name, GameEntity game){
        ActivityEntity activityEntity = new ActivityEntity(name, game.getId());
        activityManager.add(activityEntity);
        Optional<ActivityEntity> optional = activityManager.get(name, game.getId());
        return optional.orElseThrow(() -> new RuntimeException("ActivityEntity not saved"));
    }

    private UserEntity addUser(String username, String password, String email, boolean admin){
        UserEntity userEntity = new UserEntity(admin, email, password, username);
        userManager.add(userEntity);
        Optional<UserEntity> optional = userManager.getByEmail(email);
        return optional.orElseThrow(() -> new RuntimeException("UserEntity not saved"));
    }

    private GroupEntity addGroup(int slots, ActivityEntity activity, UserEntity owner){
        GroupEntity groupEntity = new GroupEntity(slots, activity.getId(), null, null, owner.getId());
        int id = groupManager.add(groupEntity);
        return groupManager.get(id);
    }
}