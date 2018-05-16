package persistence.manager;

import model.ActivityEntity;
import model.GameEntity;
import model.GroupEntity;
import model.UserEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.ActivityManager;
import persistence.manager.GameManager;
import persistence.manager.GroupManager;
import persistence.manager.UserManager;
import persistence.model.*;
import javax.inject.Inject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);

        final int slots = 5;

        GroupEntity group = addGroup(slots, activity, user);

        assertThat(group.getActivityId(), is(activity.getId()));

        assertThat(group.getSlots(), is(slots));

        Integer ownerID = groupManager.getGroupOwner(group.getId());

        assertThat(ownerID, is(user.getId()));

        List<Integer> members = groupManager.getGroupMembers(group.getId());

        assertThat(members.size(), is(1));
        assertTrue(members.contains(user.getId()));
    }

    @Test
    public void addMember(){
        final String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        UserEntity owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        UserEntity member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, activity, owner).getId();

        groupManager.addMemberToGroup(id, member2.getId());

        Set<UserEntity> users = groupManager.getGroupMembers(id)
                .stream()
                .map(userManager::getUser)
                .collect(Collectors.toSet());

        assertTrue(users.contains(owner));
        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));
    }

    @Test
    public void removeMember(){
        final String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        UserEntity owner = addUser("wewey", "123123", "xyz@lfg.com", false);
        UserEntity member2 = addUser("member", "member123", "member@lfg.com", false);

        int id = addGroup(5, activity, owner).getId();

        groupManager.addMemberToGroup(id, member2.getId());

        Set<UserEntity> users = groupManager.getGroupMembers(id)
                .stream()
                .map(userManager::getUser)
                .collect(Collectors.toSet());

        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        groupManager.removeMemberFromGroup(id, member2.getId());

        assertFalse(users.contains(member2));
        assertThat(users.size(), is(1));
    }

    @Test
    public void canDeleteActivity(){
        final String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);

        final int slots = 5;

        GroupEntity group = addGroup(slots, activity, user);

        activityManager.deleteActivity(activity.getId());

        assertNull(activityManager.getActivity(activity.getId()));
        assertNull(groupManager.getGroup(group.getId()));
    }

    @Test
    public void canDeleteGame(){
        final String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);

        final int slots = 5;

        GroupEntity group = addGroup(slots, activity, user);

        gameManager.deleteGame(game.getId());

        assertNull(gameManager.getGame(game.getId()));
        assertNull(activityManager.getActivity(activity.getId()));
        assertNull(groupManager.getGroup(group.getId()));
    }

    @Test
    public void deletes(){
        final String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        final String activityName = "Ranked";
        ActivityEntity activity = addActivity(activityName, game);

        final String username = "wewey";
        final String password = "123123";
        final String email = "xyz@lfg.com";
        final boolean admin = false;
        UserEntity user = addUser(username, password, email, admin);

        final int slots = 5;

        GroupEntity group = addGroup(slots, activity, user);

        groupManager.deleteGroup(group.getId());
        assertNull(groupManager.getGroup(group.getId()));
    }



    private GameEntity addGame(String name){
        int id = gameManager.addGame(name, null);
        return gameManager.getGame(id);
    }

    private ActivityEntity addActivity(String name, GameEntity game){
        int id = activityManager.addActivity(name, game.getId());
        return activityManager.getActivity(id);
    }

    private UserEntity addUser(String username, String password, String email, boolean admin){
        int id = userManager.addUser(username, password, email, admin);
        return userManager.getUser(id);
    }

    private GroupEntity addGroup(int slots, ActivityEntity activity, UserEntity owner){
        int id = groupManager.addGroup(slots, activity.getId(), owner.getId(), null, null);
        return groupManager.getGroup(id);
    }
}