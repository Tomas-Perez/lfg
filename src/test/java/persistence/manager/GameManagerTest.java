package persistence.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.gradle.archive.importer.embedded.EmbeddedGradleImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import persistence.manager.exception.ConstraintException;
import persistence.manager.patcher.GamePatcher;
import persistence.model.Game;

import javax.inject.Inject;

import java.util.Arrays;
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
public class GameManagerTest {
    @Inject
    private GameManager manager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Test
    public void shouldCreateManager() {
        assertNotNull(manager);
    }

    @Test
    public void addGame() {
        manager.wipeAllRecords();

        String name = "Overwatch";
        manager.addGame(name, null);
        Optional<Game> optional = manager.getByName(name);
        assertTrue(optional.isPresent());

        Game game = optional.get();

        assertThat(game.getName(), is(name));
        assertNull(game.getImage());

        manager.wipeAllRecords();
    }

    @Test
    public void gameExists() {
        manager.wipeAllRecords();

        String name = "Overwatch";
        assertFalse(manager.gameExists(name));
        manager.addGame(name, null);
        assertTrue(manager.gameExists(name));

        manager.wipeAllRecords();
    }

    @Test
    public void listGames(){
        manager.wipeAllRecords();

        String[] expected = addAll("Overwatch", "FIFA", "God of War");

        List<String> actual = manager.listGames().stream().map(Game::getName).collect(Collectors.toList());

        assertThat(actual, hasItems(expected));

        manager.wipeAllRecords();
    }

    @Test
    public void duplicateGameExc(){
        manager.wipeAllRecords();

        String name = "Overwatch";

        manager.addGame(name, null);

        assertTrue(manager.gameExists(name));
        try {
            manager.addGame(name, null);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(name));
        }

        manager.wipeAllRecords();
    }

    @Test
    public void duplicateGameExcUpdate() {
        manager.wipeAllRecords();

        String oldName = "Overwatch";
        String newName = "FIFA";

        addAll(oldName, newName, "God of War");

        Game ow = manager.getByName(oldName).get();

        assertTrue(manager.gameExists(newName));

        GamePatcher patcher = new GamePatcher.Builder().withName(newName).build();

        try {
            manager.updateGame(ow.getId(), patcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(newName));
        }

        manager.wipeAllRecords();
    }

    private String[] addAll(String... strings){
        Arrays.stream(strings).forEach(name -> manager.addGame(name, null));
        return strings;
    }
}