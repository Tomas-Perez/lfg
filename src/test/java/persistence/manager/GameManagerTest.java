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
import persistence.entity.GameEntity;

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
        manager.wipe();
    }

    @Test
    public void addGame() {
        String name = "Overwatch";
        GameEntity game = new GameEntity(null, name);
        int id = manager.add(game);
        GameEntity actualGame = manager.get(id);
        assertNotNull(actualGame);
        assertThat(actualGame.getName(), is(game.getName()));
        assertThat(actualGame.getImage(), is(game.getImage()));
    }

    @Test
    public void gameExists() {
        String name = "Overwatch";
        GameEntity game = new GameEntity(null, name);
        assertFalse(manager.exists(name));
        manager.add(game);
        assertTrue(manager.exists(name));
    }

    @Test
    public void listGames(){
        List<GameEntity> expected = addAllGames("Overwatch", "FIFA", "God of War");
        List<GameEntity> actual = manager.list()
                .stream()
                .map(manager::get)
                .collect(Collectors.toList());
        Set<GameEntity> expectedSet = new HashSet<>(expected);
        Set<GameEntity> actualSet = new HashSet<>(actual);

        assertThat(expectedSet, is(actualSet));
    }

    @Test(expected = ConstraintException.class)
    public void duplicateGameExc(){
        String name = "Overwatch";

        GameEntity game = new GameEntity(null, name);
        manager.add(game);

        assertTrue(manager.exists(name));
        manager.add(game);
    }

    @Test(expected = ConstraintException.class)
    public void duplicateGameExcUpdate() {
        String oldName = "Overwatch";
        String newName = "FIFA";

        addAllGames(oldName, newName, "God of War");

        GameEntity ow = manager.getByName(oldName).get();

        assertTrue(manager.exists(newName));

        GamePatcher patcher = new GamePatcher.Builder().withName(newName).build();

        manager.updateGame(ow.getId(), patcher);
    }

    @Test
    public void deletes(){
        GameEntity game = addGame("Overwatch");
        manager.delete(game.getId());
        assertNull(manager.get(game.getId()));
    }

    private GameEntity addGame(String name){
        GameEntity game = new GameEntity(null, name);
        int id = manager.add(game);
        return manager.get(id);
    }

    private List<GameEntity> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGame).collect(Collectors.toList());
    }
}