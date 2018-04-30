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

import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Tomas Perez Molina
 */
@RunWith(Arquillian.class)
public class GroupManagerTest {
    @Inject
    private UserManager userManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private GameManager gameManager;

    @Inject
    private GroupManager groupManager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Test
    public void shouldCreateManager() {
        assertNotNull(groupManager);
        assertNotNull(activityManager);
        assertNotNull(gameManager);
        assertNotNull(userManager);
    }

    @Test
    public void addGroup(){
        cleanUp();
        assertThat(groupManager.listGroups().size(), is(0));

        final String gameName = "Overwatch";
        Game ow = addGame(gameName);

        final String activityName = "Ranked";
        Activity ranked = addActivity(activityName, ow);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);

        final int slots = 5;

        Group group = addGroup(slots, ranked, user);

        assertThat(group.getActivity(), is(ranked));

        assertThat(group.getSlots(), is(slots));

        assertThat(group.getOwner(), is(user));

        assertThat(group.getMembers().size(), is(1));
        assertTrue(group.getMembers().contains(user));

        cleanUp();
    }

    @Test
    public void addMember(){
        cleanUp();

        final String gameName = "Overwatch";
        Game ow = addGame(gameName);

        final String activityName = "Ranked";
        Activity ranked = addActivity(activityName, ow);

        User owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        User member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, ranked, owner).getId();

        groupManager.addMemberToGroup(id, member2);

        Set<User> users = groupManager.getGroup(id).getMembers();

        assertTrue(users.contains(owner));
        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        cleanUp();
    }

    @Test
    public void removeMember(){
        cleanUp();

        final String gameName = "Overwatch";
        Game ow = addGame(gameName);

        final String activityName = "Ranked";
        Activity ranked = addActivity(activityName, ow);

        User owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        User member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, ranked, owner).getId();

        groupManager.addMemberToGroup(id, member2);

        Set<User> users = groupManager.getGroup(id).getMembers();

        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        groupManager.removeMemberFromGroup(id, member2);

        assertFalse(users.contains(member2));
        assertThat(users.size(), is(1));

        cleanUp();
    }

    private void cleanUp(){
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