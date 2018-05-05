package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
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

    @Before
    @After
    public void setup(){
        groupManager.wipeAllRecords();
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
        userManager.wipeAllRecords();
    }

    @Test
    public void addGroup(){
        assertThat(groupManager.listGroups().size(), is(0));

        final String gameName = "Overwatch";
        Game game = addGame(gameName);

        final String activityName = "Ranked";
        Activity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);

        final int slots = 5;

        Group group = addGroup(slots, activity, user);

        assertThat(group.getActivity(), is(activity));

        assertThat(group.getSlots(), is(slots));

        assertThat(group.getOwner(), is(user));

        assertThat(group.getMembers().size(), is(1));
        assertTrue(group.getMembers().contains(user));
    }

    @Test
    public void addMember(){
        final String gameName = "Overwatch";
        Game game = addGame(gameName);

        final String activityName = "Ranked";
        Activity activity = addActivity(activityName, game);

        User owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        User member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, activity, owner).getId();

        groupManager.addMemberToGroup(id, member2);

        Set<User> users = groupManager.getGroup(id).getMembers();

        assertTrue(users.contains(owner));
        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));
    }

    @Test
    public void removeMember(){
        final String gameName = "Overwatch";
        Game game = addGame(gameName);

        final String activityName = "Ranked";
        Activity activity = addActivity(activityName, game);

        User owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        User member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, activity, owner).getId();

        groupManager.addMemberToGroup(id, member2);

        Set<User> users = groupManager.getGroup(id).getMembers();

        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        groupManager.removeMemberFromGroup(id, member2);

        assertFalse(users.contains(member2));
        assertThat(users.size(), is(1));
    }

    @Test
    public void canDeleteActivity(){
        final String gameName = "Overwatch";
        Game game = addGame(gameName);

        final String activityName = "Ranked";
        Activity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);

        final int slots = 5;

        Group group = addGroup(slots, activity, user);

        activityManager.deleteActivity(activity.getId());

        Group groupActual = groupManager.getGroup(group.getId());
        System.out.println(groupActual.getActivity());

        assertNull(activityManager.getActivity(activity.getId()));
        assertNull(groupActual);
    }

    @Test
    public void canDeleteGame(){
        final String gameName = "Overwatch";
        Game game = addGame(gameName);

        final String activityName = "Ranked";
        Activity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        User user = addUser(username, password, email, admin);

        final int slots = 5;

        Group group = addGroup(slots, activity, user);

        gameManager.deleteGame(game.getId());

        assertNull(groupManager.getGroup(group.getId()));
    }



    private Game addGame(String name){
        int id = gameManager.addGame(name, null);
        return gameManager.getGame(id);
    }

    private Activity addActivity(String name, Game game){
        int id = activityManager.addActivity(name, game);
        return activityManager.getActivity(id);
    }

    private User addUser(String username, String password, String email, boolean admin){
        int id = userManager.addUser(username, password, email, admin);
        return userManager.getUser(id);
    }

    private Group addGroup(int slots, Activity activity, User owner){
        int id = groupManager.addGroup(slots, activity, owner, null, null);
        return groupManager.getGroup(id);
    }
}