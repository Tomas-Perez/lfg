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
import persistence.manager.exception.ConstraintException;
import persistence.manager.generator.KeyGenerator;
import persistence.manager.patcher.ActivityPatcher;
import persistence.entity.ActivityEntity;
import persistence.entity.GameEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class ActivityManagerTest {
    @Inject
    private EntityManagerProducer entityManagerProducer;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private GameManager gameManager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Test
    public void shouldCreateManager() {
        assertNotNull(activityManager);
        assertNotNull(gameManager);
    }

    @Before
    @After
    public void setup(){
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void addActivity() {
        String gameName = "Overwatch";
        GameEntity game = addGame(gameName);


        String activityName = "Ranked";
        activityManager.addActivity(activityName, game.getId());
        Optional<ActivityEntity> optionalActivity = activityManager.getActivity(activityName, game.getId());
        assertTrue(optionalActivity.isPresent());

        ActivityEntity activity = optionalActivity.get();
        assertThat(activity.getName(), is(activityName));
        assertThat(activity.getGameId(), is(game.getId()));
    }

    @Test
    public void activityExists() {
        String gameName = "Overwatch";
        GameEntity game = addGame(gameName);

        String activityName = "Ranked";
        assertFalse(activityManager.activityExists(activityName, game.getId()));
        activityManager.addActivity(activityName, game.getId());
        assertTrue(activityManager.activityExists(activityName, game.getId()));
    }

    @Test
    public void updateActivity(){
        String owName = "Overwatch";
        String godOfWarName = "God of War";
        String fifaName = "FIFA";

        List<GameEntity> games = addAllGames(owName, godOfWarName, fifaName);

        GameEntity ow = games.get(0);
        GameEntity godOfWar = games.get(1);
        GameEntity fifa = games.get(2);

        String ranked = "Ranked";
        String casual = "Casual";
        String tournament = "Tournament";

        addAllActivities(
                new ActivityGamePair(ranked, ow.getId()),
                new ActivityGamePair(casual, ow.getId()),
                new ActivityGamePair(ranked, godOfWar.getId())
        );

        ActivityEntity activity = activityManager
                .getActivity(ranked, godOfWar.getId())
                .orElseThrow(() -> new RuntimeException("ActivityEntity not saved"));

        assertTrue(activityManager.activityExists(ranked, godOfWar.getId()));
        assertFalse(activityManager.activityExists(tournament, fifa.getId()));

        ActivityPatcher fifaPatcher = new ActivityPatcher.Builder().withName(tournament).withGame(fifa.getId()).build();
        activityManager.updateActivity(activity.getId(), fifaPatcher);

        assertFalse(activityManager.activityExists(ranked, godOfWar.getId()));
        assertTrue(activityManager.activityExists(tournament, fifa.getId()));

        System.out.println(activityManager.listActivities());
    }

    @Test
    public void listActivities(){
        List<GameEntity> games = addAllGames("Overwatch", "FIFA", "God of War");

        ActivityGamePair[] expected = addAllActivitiesGetPair(
                new ActivityGamePair("Ranked", games.get(0).getId()),
                new ActivityGamePair("Tournament", games.get(1).getId()),
                new ActivityGamePair("Campaign", games.get(2).getId())
        );

        List<ActivityGamePair> actual = activityManager.listActivities()
                .stream()
                .map(activityManager::getActivity)
                .map(activity -> new ActivityGamePair(activity.getName(), activity.getGameId()))
                .collect(Collectors.toList());

        assertThat(actual, hasItems(expected));
    }

    @Test
    public void duplicateGameExc(){
        String owName = "Overwatch";
        String godOfWarName = "God of War";

        List<GameEntity> games = addAllGames(owName, godOfWarName);
        GameEntity ow = games.get(0);
        GameEntity godOfWar = games.get(1);

        String ranked = "Ranked";
        String casual = "Casual";

        addAllActivities(
                new ActivityGamePair(ranked, ow.getId()),
                new ActivityGamePair(casual, ow.getId()), //Same game different activity
                new ActivityGamePair(ranked, godOfWar.getId()) //Same activity different game
        );

        assertTrue(activityManager.activityExists(ranked, ow.getId()));
        assertTrue(activityManager.activityExists(casual, ow.getId()));
        assertTrue(activityManager.activityExists(ranked, godOfWar.getId()));

        try {
            activityManager.addActivity(ranked, ow.getId()); //Same activity and game
            fail();
        } catch (ConstraintException exc){}
    }

    @Test
    public void duplicateGameExcUpdate() {
        String owName = "Overwatch";
        String godOfWarName = "God of War";

        List<GameEntity> games = addAllGames(owName, godOfWarName);
        GameEntity ow = games.get(0);
        GameEntity godOfWar = games.get(1);

        String ranked = "Ranked";
        String casual = "Casual";

        addAllActivities(
                new ActivityGamePair(ranked, ow.getId()),
                new ActivityGamePair(casual, ow.getId()), //Same game different activity
                new ActivityGamePair(ranked, godOfWar.getId()) //Same activity different game
        );

        List<ActivityEntity> activities = activityManager.listActivities()
                .stream()
                .map(activityManager::getActivity)
                .collect(Collectors.toList());

        ActivityPatcher namePatcher = new ActivityPatcher.Builder().withName(ranked).build();

        try {
            activityManager.updateActivity(activities.get(1).getId(), namePatcher);
            fail();
        } catch (ConstraintException exc){}

        ActivityPatcher gamePatcher = new ActivityPatcher.Builder().withGame(ow.getId()).build();

        try {
            activityManager.updateActivity(activities.get(2).getId(), gamePatcher);
            fail();
        } catch (ConstraintException exc){}
    }

    @Test
    public void addsActivitiesToGame(){
        String owName = "Overwatch";
        GameEntity ow = addGame(owName);

        String ranked = "Ranked";
        String casual = "Casual";
        String ptr = "PTR";

        List<ActivityEntity> expectedList = addAllActivities(
                new ActivityGamePair(ranked, ow.getId()),
                new ActivityGamePair(casual, ow.getId()),
                new ActivityGamePair(ptr, ow.getId())
        );

        Set<ActivityEntity> actual = gameManager.getGameActivities(ow.getId())
                .stream()
                .map(activityManager::getActivity)
                .collect(Collectors.toSet());
        Set<ActivityEntity> expected = new HashSet<>(expectedList);

        assertThat(expected, is(actual));
    }

    @Test
    public void canDeleteGame(){
        EntityManager gameEM = entityManagerProducer.createEntityManager();
        EntityManager activityEM = entityManagerProducer.createEntityManager();
        final EntityManager keyGenEM = entityManagerProducer.createEntityManager();
        KeyGenerator keyGen = new KeyGenerator(keyGenEM);
        GameManager gameManager = new GameManager(gameEM, keyGen);
        ActivityManager activityManager = new ActivityManager(activityEM, keyGen, gameManager);

        String owName = "Overwatch";
        GameEntity ow = addGame(owName);

        String ranked = "Ranked";
        String casual = "Casual";
        String ptr = "PTR";

        List<ActivityEntity> activities = addAllActivities(
                new ActivityGamePair(ranked, ow.getId()),
                new ActivityGamePair(casual, ow.getId()),
                new ActivityGamePair(ptr, ow.getId())
        );

        activities.stream()
                .map(ActivityEntity::getId)
                .forEach(id -> assertNotNull(activityManager.getActivity(id)));

        gameManager.deleteGame(ow.getId());

        entityManagerProducer.closeEntityManager(activityEM);
        entityManagerProducer.closeEntityManager(gameEM);
        EntityManager gameEM2 = entityManagerProducer.createEntityManager();
        EntityManager activityEM2 = entityManagerProducer.createEntityManager();
        GameManager gameManager2 = new GameManager(gameEM2, keyGen);
        ActivityManager activityManager2 = new ActivityManager(activityEM2, keyGen, gameManager2);

        activities.stream()
                .map(ActivityEntity::getId)
                .forEach(id -> assertNull(activityManager2.getActivity(id)));

        entityManagerProducer.closeEntityManager(gameEM2);
        entityManagerProducer.closeEntityManager(activityEM2);
        entityManagerProducer.closeEntityManager(keyGenEM);
    }

    @Test
    public void deletes(){
        GameEntity game = addGame("Overwatch");
        ActivityEntity activity = addActivity(new ActivityGamePair("Ranked", game.getId()));
        activityManager.deleteActivity(activity.getId());
        assertNull(activityManager.getActivity(activity.getId()));
    }

    private GameEntity addGame(String name){
        int id = gameManager.addGame(name, null);
        return gameManager.getGame(id);
    }

    private List<GameEntity> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGame).collect(Collectors.toList());
    }

    private ActivityEntity addActivity(ActivityGamePair pair){
        int id = activityManager.addActivity(pair.name, pair.game);
        return activityManager.getActivity(id);
    }

    private List<ActivityEntity> addAllActivities(ActivityGamePair... pairs){
        return Arrays.stream(pairs).map(this::addActivity).collect(Collectors.toList());
    }

    private ActivityGamePair[] addAllActivitiesGetPair(ActivityGamePair... pairs){
        Arrays.stream(pairs).forEach(pair -> activityManager.addActivity(pair.name, pair.game));
        return pairs;
    }

    private class ActivityGamePair{
        String name;
        Integer game;

        ActivityGamePair(String name, Integer game) {
            this.name = name;
            this.game = game;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActivityGamePair)) return false;
            ActivityGamePair that = (ActivityGamePair) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(game, that.game);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name, game);
        }
    }
}