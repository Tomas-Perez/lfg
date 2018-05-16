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
import persistence.manager.patcher.GamePatcher;
import model.GameEntity;

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

    @Before
    @After
    public void setup(){
        manager.wipeAllRecords();
    }

    @Test
    public void addGame() {
        String name = "Overwatch";
        int id = manager.addGame(name, null);
        GameEntity game = manager.getGame(id);
        assertNotNull(game);
        assertThat(game.getName(), is(name));
        assertNull(game.getImage());
    }

    @Test
    public void gameExists() {
        String name = "Overwatch";
        assertFalse(manager.gameExists(name));
        manager.addGame(name, null);
        assertTrue(manager.gameExists(name));
    }

    @Test
    public void listGames(){
        List<GameEntity> expected = addAllGames("Overwatch", "FIFA", "God of War");
        List<GameEntity> actual = manager.listGames()
                .stream()
                .map(manager::getGame)
                .collect(Collectors.toList());
        Set<GameEntity> expectedSet = new HashSet<>(expected);
        Set<GameEntity> actualSet = new HashSet<>(actual);

        assertThat(expectedSet, is(actualSet));
    }

    @Test
    public void duplicateGameExc(){
        String name = "Overwatch";

        manager.addGame(name, null);

        assertTrue(manager.gameExists(name));
        try {
            manager.addGame(name, null);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(name));
        }
    }

    @Test
    public void duplicateGameExcUpdate() {
        String oldName = "Overwatch";
        String newName = "FIFA";

        addAllGames(oldName, newName, "God of War");

        GameEntity ow = manager.getByName(oldName).get();

        assertTrue(manager.gameExists(newName));

        GamePatcher patcher = new GamePatcher.Builder().withName(newName).build();

        try {
            manager.updateGame(ow.getId(), patcher);
            fail();
        } catch (ConstraintException exc){
            assertThat(exc.getConstraintName(), is(newName));
        }
    }

    @Test
    public void deletes(){
        GameEntity game = addGame("Overwatch");
        manager.deleteGame(game.getId());
        assertNull(manager.getGame(game.getId()));
    }

    private GameEntity addGame(String name){
        int id = manager.addGame(name, null);
        return manager.getGame(id);
    }

    private List<GameEntity> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGame).collect(Collectors.toList());
    }
}