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
import persistence.manager.patcher.ActivityPatcher;
import persistence.model.Activity;
import persistence.model.Game;

import javax.inject.Inject;

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
        Game game = addGame(gameName);


        String activityName = "Ranked";
        activityManager.addActivity(activityName, game);
        Optional<Activity> optionalActivity = activityManager.getActivity(activityName, game);
        assertTrue(optionalActivity.isPresent());

        Activity activity = optionalActivity.get();
        assertThat(activity.getName(), is(activityName));
        assertThat(activity.getGame(), is(game));
    }

    @Test
    public void activityExists() {
        String gameName = "Overwatch";
        Game game = addGame(gameName);

        String activityName = "Ranked";
        assertFalse(activityManager.activityExists(activityName, game));
        activityManager.addActivity(activityName, game);
        assertTrue(activityManager.activityExists(activityName, game));
    }

    @Test
    public void updateActivity(){
        String owName = "Overwatch";
        String godOfWarName = "God of War";
        String fifaName = "FIFA";

        List<Game> games = addAllGames(owName, godOfWarName, fifaName);

        Game ow = games.get(0);
        Game godOfWar = games.get(1);
        Game fifa = games.get(2);

        String ranked = "Ranked";
        String casual = "Casual";
        String tournament = "Tournament";

        addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow),
                new ActivityGamePair(ranked, godOfWar)
        );

        Activity activity = activityManager
                .getActivity(ranked, godOfWar)
                .orElseThrow(() -> new RuntimeException("Activity not saved"));

        assertTrue(activityManager.activityExists(ranked, godOfWar));
        assertFalse(activityManager.activityExists(tournament, fifa));

        ActivityPatcher fifaPatcher = new ActivityPatcher.Builder().withName(tournament).withGame(fifa).build();
        activityManager.updateActivity(activity.getId(), fifaPatcher);

        assertFalse(activityManager.activityExists(ranked, godOfWar));
        assertTrue(activityManager.activityExists(tournament, fifa));
    }

    @Test
    public void listActivities(){
        List<Game> games = addAllGames("Overwatch", "FIFA", "God of War");

        ActivityGamePair[] expected = addAllActivitiesGetPair(
                new ActivityGamePair("Ranked", games.get(0)),
                new ActivityGamePair("Tournament", games.get(1)),
                new ActivityGamePair("Campaign", games.get(2))
        );

        List<ActivityGamePair> actual = activityManager.listActivities()
                .stream()
                .map(activity -> new ActivityGamePair(activity.getName(), activity.getGame()))
                .collect(Collectors.toList());

        assertThat(actual, hasItems(expected));
    }

    @Test
    public void duplicateGameExc(){
        String owName = "Overwatch";
        String godOfWarName = "God of War";

        List<Game> games = addAllGames(owName, godOfWarName);
        Game ow = games.get(0);
        Game godOfWar = games.get(1);

        String ranked = "Ranked";
        String casual = "Casual";

        addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow), //Same game different activity
                new ActivityGamePair(ranked, godOfWar) //Same activity different game
        );

        assertTrue(activityManager.activityExists(ranked, ow));
        assertTrue(activityManager.activityExists(casual, ow));
        assertTrue(activityManager.activityExists(ranked, godOfWar));

        try {
            activityManager.addActivity(ranked, ow); //Same activity and game
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(String.format("%s for %s", ranked, ow.getName())));
        }
    }

    @Test
    public void duplicateGameExcUpdate() {
        String owName = "Overwatch";
        String godOfWarName = "God of War";

        List<Game> games = addAllGames(owName, godOfWarName);
        Game ow = games.get(0);
        Game godOfWar = games.get(1);

        String ranked = "Ranked";
        String casual = "Casual";

        addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow), //Same game different activity
                new ActivityGamePair(ranked, godOfWar) //Same activity different game
        );

        List<Activity> activities = activityManager.listActivities();

        ActivityPatcher namePatcher = new ActivityPatcher.Builder().withName(ranked).build();

        try {
            activityManager.updateActivity(activities.get(1).getId(), namePatcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(String.format("%s for %s", ranked, ow.getName())));
        }

        ActivityPatcher gamePatcher = new ActivityPatcher.Builder().withGame(ow).build();

        try {
            activityManager.updateActivity(activities.get(2).getId(), gamePatcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(String.format("%s for %s", ranked, ow.getName())));
        }
    }

    @Test
    public void addsActivitiesToGame(){
        String owName = "Overwatch";
        Game ow = addGame(owName);

        String ranked = "Ranked";
        String casual = "Casual";
        String ptr = "PTR";

        List<Activity> expectedList = addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow),
                new ActivityGamePair(ptr, ow)
        );

        Game actualOw = gameManager.getGame(ow.getId());
        Set<Activity> actual = actualOw.getActivities();
        Set<Activity> expected = new HashSet<>(expectedList);

        assertThat(expected, is(actual));
    }

    @Test
    public void canDeleteGame(){
        String owName = "Overwatch";
        Game ow = addGame(owName);

        String ranked = "Ranked";
        String casual = "Casual";
        String ptr = "PTR";

        List<Activity> activities = addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow),
                new ActivityGamePair(ptr, ow)
        );

        gameManager.deleteGame(ow.getId());

        activities.stream()
                .map(Activity::getId)
                .forEach(id -> assertNull(activityManager.getActivity(id)));
    }

    @Test
    public void deletes(){
        Game game = addGame("Overwatch");
        Activity activity = addActivity(new ActivityGamePair("Ranked", game));
        Activity gameActivity = game.getActivities().iterator().next();
        System.out.println("Equals? = " + activity.equals(gameActivity));
        int expected = activity.hashCode();
        int actual = gameActivity.hashCode();
        System.out.println("actual = " + actual);
        System.out.println("expected = " + expected);
        assertThat(actual, is(expected));
        System.out.println("activity = " + activity);
        System.out.println("gameActivity = " + gameActivity);
        assertThat(gameActivity, is(activity));
        System.out.println("Contains activity? = " + game.getActivities().contains(activity));
        System.out.println("Contains gameActivity? = " + game.getActivities().contains(gameActivity));



        activityManager.deleteActivity(activity.getId());
        assertNull(activityManager.getActivity(activity.getId()));
    }

    private Game addGame(String name){
        int id = gameManager.addGame(name, null);
        return gameManager.getGame(id);
    }

    private List<Game> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGame).collect(Collectors.toList());
    }

    private Activity addActivity(ActivityGamePair pair){
        int id = activityManager.addActivity(pair.name, pair.game);
        return activityManager.getActivity(id);
    }

    private List<Activity> addAllActivities(ActivityGamePair... pairs){
        return Arrays.stream(pairs).map(this::addActivity).collect(Collectors.toList());
    }

    private ActivityGamePair[] addAllActivitiesGetPair(ActivityGamePair... pairs){
        Arrays.stream(pairs).forEach(pair -> activityManager.addActivity(pair.name, pair.game));
        return pairs;
    }

    private class ActivityGamePair{
        String name;
        Game game;

        ActivityGamePair(String name, Game game) {
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