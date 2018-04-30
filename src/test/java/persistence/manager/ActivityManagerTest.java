package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.ActivityPatcher;
import persistence.model.Activity;
import persistence.model.Game;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Test
    public void addActivity() {
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

        String gameName = "Overwatch";
        Game game = addGame(gameName);


        String activityName = "Ranked";
        activityManager.addActivity(activityName, game);
        Optional<Activity> optionalActivity = activityManager.getActivity(activityName, game);
        assertTrue(optionalActivity.isPresent());

        Activity activity = optionalActivity.get();
        assertThat(activity.getName(), is(activityName));
        assertThat(activity.getGame(), is(game));

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void activityExists() {
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

        String gameName = "Overwatch";
        Game game = addGame(gameName);

        String activityName = "Ranked";
        assertFalse(activityManager.activityExists(activityName, game));
        activityManager.addActivity(activityName, game);
        assertTrue(activityManager.activityExists(activityName, game));

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void updateActivity(){
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

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

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void listActivities(){
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

        List<Game> games = addAllGames("Overwatch", "FIFA", "God of War");

        ActivityGamePair[] expected = addAllActivities(
                new ActivityGamePair("Ranked", games.get(0)),
                new ActivityGamePair("Tournament", games.get(1)),
                new ActivityGamePair("Campaign", games.get(2))
        );

        List<ActivityGamePair> actual = activityManager.listActivities()
                .stream()
                .map(activity -> new ActivityGamePair(activity.getName(), activity.getGame()))
                .collect(Collectors.toList());

        assertThat(actual, hasItems(expected));

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void duplicateGameExc(){
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

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

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void duplicateGameExcUpdate() {
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

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

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    @Test
    public void addsActivitiesToGame(){
        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();

        String owName = "Overwatch";
        Game ow = addGame(owName);

        String ranked = "Ranked";
        String casual = "Casual";
        String ptr = "PTR";

        addAllActivities(
                new ActivityGamePair(ranked, ow),
                new ActivityGamePair(casual, ow),
                new ActivityGamePair(ptr, ow)
        );

        Game actualOw = gameManager.getByName(owName).get();

        System.out.println(actualOw.getActivities());

        activityManager.wipeAllRecords();
        gameManager.wipeAllRecords();
    }

    private List<Game> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGame).collect(Collectors.toList());
    }

    private ActivityGamePair[] addAllActivities(ActivityGamePair... pairs){
        Arrays.stream(pairs).forEach(pair -> activityManager.addActivity(pair.name, pair.game));
        return pairs;
    }

    private Game addGame(String name){
        gameManager.addGame(name, null);
        Optional<Game> optional = gameManager.getByName(name);
        return optional.orElseThrow(() -> new RuntimeException("Game not saved"));
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