package persistence.manager;

import persistence.entity.ActivityEntity;
import persistence.entity.GameEntity;
import persistence.entity.GroupEntity;
import persistence.entity.UserEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;

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

    @Inject
    private EntityManagerProducer entityManagerProducer;

    @Inject
    private FriendHelperManager friendHelper;

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
        groupManager.wipe();
        activityManager.wipe();
        gameManager.wipe();
        userManager.wipe();
    }

    @Test
    public void addGroup(){
        assertThat(groupManager.list().size(), is(0));

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
                .map(userManager::get)
                .collect(Collectors.toSet());

        assertTrue(users.contains(owner));
        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));
    }

    @Test
    public void removeMember(){
        final EntityManager gameEM = entityManagerProducer.createEntityManager();
        final EntityManager activityEM = entityManagerProducer.createEntityManager();
        final EntityManager userEM = entityManagerProducer.createEntityManager();
        final EntityManager groupEM = entityManagerProducer.createEntityManager();
        GameManager gameManager = new GameManager(gameEM);
        ActivityManager activityManager = new ActivityManager(activityEM, gameManager);
        UserManager userManager = new UserManager(userEM, friendHelper);
        GroupManager groupManager = new GroupManager(groupEM, userManager, activityManager);

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
                .map(userManager::get)
                .collect(Collectors.toSet());

        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        groupManager.removeMemberFromGroup(id, member2.getId());

        entityManagerProducer.closeEntityManager(groupEM);
        final EntityManager groupEM2 = entityManagerProducer.createEntityManager();
        GroupManager groupManager2 = new GroupManager(groupEM2, userManager, activityManager);

        Set<UserEntity> users2 = groupManager2.getGroupMembers(id)
                .stream()
                .map(userManager::get)
                .collect(Collectors.toSet());

        assertFalse(users2.contains(member2));
        assertThat(users2.size(), is(1));

        entityManagerProducer.closeEntityManager(gameEM);
        entityManagerProducer.closeEntityManager(groupEM2);
        entityManagerProducer.closeEntityManager(activityEM);
        entityManagerProducer.closeEntityManager(userEM);
    }

    @Test
    public void canDeleteActivity(){
        final EntityManager gameEM = entityManagerProducer.createEntityManager();
        final EntityManager activityEM = entityManagerProducer.createEntityManager();
        final EntityManager userEM = entityManagerProducer.createEntityManager();
        final EntityManager groupEM = entityManagerProducer.createEntityManager();
        GameManager gameManager = new GameManager(gameEM);
        ActivityManager activityManager = new ActivityManager(activityEM, gameManager);
        UserManager userManager = new UserManager(userEM, friendHelper);

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

        activityManager.delete(activity.getId());
        entityManagerProducer.closeEntityManager(activityEM);

        final EntityManager activityEM2 = entityManagerProducer.createEntityManager();
        ActivityManager activityManager2 = new ActivityManager(activityEM2, gameManager);
        GroupManager groupManager = new GroupManager(groupEM, userManager, activityManager2);

        assertNull(activityManager2.get(activity.getId()));
        assertNull(groupManager.get(group.getId()));

        entityManagerProducer.closeEntityManager(gameEM);
        entityManagerProducer.closeEntityManager(groupEM);
        entityManagerProducer.closeEntityManager(activityEM2);
        entityManagerProducer.closeEntityManager(userEM);
    }

    @Test
    public void deleteGroupWhenLeaderLeaves(){
        final EntityManager gameEM = entityManagerProducer.createEntityManager();
        final EntityManager activityEM = entityManagerProducer.createEntityManager();
        final EntityManager userEM = entityManagerProducer.createEntityManager();
        final EntityManager groupEM = entityManagerProducer.createEntityManager();
        GameManager gameManager = new GameManager(gameEM);
        ActivityManager activityManager = new ActivityManager(activityEM, gameManager);
        UserManager userManager = new UserManager(userEM, friendHelper);
        GroupManager groupManager = new GroupManager(groupEM, userManager, activityManager);

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
                .map(userManager::get)
                .collect(Collectors.toSet());

        assertTrue(users.contains(member2));
        assertThat(users.size(), is(2));

        groupManager.removeMemberFromGroup(id, owner.getId());

        entityManagerProducer.closeEntityManager(groupEM);
        final EntityManager groupEM2 = entityManagerProducer.createEntityManager();
        GroupManager groupManager2 = new GroupManager(groupEM2, userManager, activityManager);

        assertNull(groupManager2.get(id));

        entityManagerProducer.closeEntityManager(gameEM);
        entityManagerProducer.closeEntityManager(groupEM2);
        entityManagerProducer.closeEntityManager(activityEM);
        entityManagerProducer.closeEntityManager(userEM);
    }

    @Test
    public void canDeleteGame(){
        final EntityManager gameEM = entityManagerProducer.createEntityManager();
        final EntityManager activityEM = entityManagerProducer.createEntityManager();
        final EntityManager userEM = entityManagerProducer.createEntityManager();
        final EntityManager groupEM = entityManagerProducer.createEntityManager();
        GameManager gameManager = new GameManager(gameEM);
        UserManager userManager = new UserManager(userEM, friendHelper);

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

        gameManager.delete(game.getId());
        entityManagerProducer.closeEntityManager(gameEM);

        final EntityManager gameEM2 = entityManagerProducer.createEntityManager();
        GameManager gameManager2 = new GameManager(gameEM2);
        ActivityManager activityManager = new ActivityManager(activityEM, gameManager2);
        GroupManager groupManager = new GroupManager(groupEM, userManager, activityManager);


        assertNull(gameManager2.get(game.getId()));
        assertNull(activityManager.get(activity.getId()));
        assertNull(groupManager.get(group.getId()));

        entityManagerProducer.closeEntityManager(gameEM2);
        entityManagerProducer.closeEntityManager(groupEM);
        entityManagerProducer.closeEntityManager(activityEM);
        entityManagerProducer.closeEntityManager(userEM);
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

        groupManager.delete(group.getId());
        assertNull(groupManager.get(group.getId()));
    }



    private GameEntity addGame(String name){
        GameEntity game = new GameEntity(null, name);
        int id = gameManager.add(game);
        return gameManager.get(id);
    }

    private ActivityEntity addActivity(String name, GameEntity game){
        ActivityEntity activity = new ActivityEntity(name, game.getId());
        int id = activityManager.add(activity);
        return activityManager.get(id);
    }

    private UserEntity addUser(String username, String password, String email, boolean admin){
        UserEntity user = new UserEntity(admin, email, password, username);
        int id = userManager.add(user);
        return userManager.get(id);
    }

    private GroupEntity addGroup(int slots, ActivityEntity activity, UserEntity owner){
        GroupEntity group = new GroupEntity(slots, activity.getId(), null, null, owner.getId());
        int id = groupManager.add(group);
        return groupManager.get(id);
    }
}