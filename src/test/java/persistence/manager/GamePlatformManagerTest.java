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
import persistence.entity.GamePlatformEntity;
import persistence.manager.exception.ConstraintException;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class GamePlatformManagerTest {
    @Inject
    private GamePlatformManager manager;

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
    public void addGamePlatform() {
        String name = "PS4";
        GamePlatformEntity gamePlatform = new GamePlatformEntity(name, null);
        int id = manager.add(gamePlatform);
        GamePlatformEntity actualGame = manager.get(id);
        assertNotNull(actualGame);
        assertThat(actualGame.getName(), is(gamePlatform.getName()));
        assertThat(actualGame.getImage(), is(gamePlatform.getImage()));
    }

    @Test
    public void gameExists() {
        String name = "PS4";
        GamePlatformEntity game = new GamePlatformEntity(name, null);
        assertFalse(manager.exists(name));
        manager.add(game);
        assertTrue(manager.exists(name));
    }

    @Test
    public void listGames(){
        List<GamePlatformEntity> expected = addAllGames("PS4", "XBOX ONE X BOX", "PC");
        List<GamePlatformEntity> actual = manager.list()
                .stream()
                .map(manager::get)
                .collect(Collectors.toList());
        Set<GamePlatformEntity> expectedSet = new HashSet<>(expected);
        Set<GamePlatformEntity> actualSet = new HashSet<>(actual);

        assertThat(expectedSet, is(actualSet));
    }

    @Test(expected = ConstraintException.class)
    public void duplicateGameExc(){
        String name = "PS4";

        GamePlatformEntity gamePlatformEntity = new GamePlatformEntity(name, null);
        manager.add(gamePlatformEntity);

        assertTrue(manager.exists(name));
        manager.add(gamePlatformEntity);
    }
//
//    @Test(expected = ConstraintException.class)
//    public void duplicateGameExcUpdate() {
//        String oldName = "PS4";
//        String newName = "XBOX";
//
//        addAllGames(oldName, newName, "PC");
//
//        GamePlatformEntity ow = manager.getByName(oldName).get();
//
//        assertTrue(manager.exists(newName));
//
//        GamePatcher patcher = new GamePatcher.Builder().withName(newName).build();
//
//        manager.updateGame(ow.getId(), patcher);
//    }

    @Test
    public void deletes(){
        GamePlatformEntity game = addGamePlatform("PS4");
        manager.delete(game.getId());
        assertNull(manager.get(game.getId()));
    }

    private GamePlatformEntity addGamePlatform(String name){
        GamePlatformEntity game = new GamePlatformEntity(name, null);
        int id = manager.add(game);
        return manager.get(id);
    }

    private List<GamePlatformEntity> addAllGames(String... names){
        return Arrays.stream(names).map(this::addGamePlatform).collect(Collectors.toList());
    }
}