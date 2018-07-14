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
import persistence.entity.ChatPlatformEntity;
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
public class ChatPlatformManagerTest {
    @Inject
    private ChatPlatformManager manager;

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
    public void addChatPlatform() {
        String name = "Discord";
        ChatPlatformEntity ChatPlatform = new ChatPlatformEntity(name, null);
        int id = manager.add(ChatPlatform);
        ChatPlatformEntity actualGame = manager.get(id);
        assertNotNull(actualGame);
        assertThat(actualGame.getName(), is(ChatPlatform.getName()));
        assertThat(actualGame.getImage(), is(ChatPlatform.getImage()));
    }

    @Test
    public void gameExists() {
        String name = "Discord";
        ChatPlatformEntity game = new ChatPlatformEntity(name, null);
        assertFalse(manager.exists(name));
        manager.add(game);
        assertTrue(manager.exists(name));
    }

    @Test
    public void listGames(){
        List<ChatPlatformEntity> expected = addAllGames("Discord", "Mumble", "Skype");
        List<ChatPlatformEntity> actual = manager.list()
                .stream()
                .map(manager::get)
                .collect(Collectors.toList());
        Set<ChatPlatformEntity> expectedSet = new HashSet<>(expected);
        Set<ChatPlatformEntity> actualSet = new HashSet<>(actual);

        assertThat(expectedSet, is(actualSet));
    }

    @Test(expected = ConstraintException.class)
    public void duplicateGameExc(){
        String name = "Discord";

        ChatPlatformEntity ChatPlatformEntity = new ChatPlatformEntity(name, null);
        manager.add(ChatPlatformEntity);

        assertTrue(manager.exists(name));
        manager.add(ChatPlatformEntity);
    }
//
//    @Test(expected = ConstraintException.class)
//    public void duplicateGameExcUpdate() {
//        String oldName = "PS4";
//        String newName = "XBOX";
//
//        addAllGames(oldName, newName, "PC");
//
//        ChatPlatformEntity ow = manager.getByName(oldName).get();
//
//        assertTrue(manager.exists(newName));
//
//        GamePatcher patcher = new GamePatcher.Builder().withName(newName).build();
//
//        manager.updateGame(ow.getId(), patcher);
//    }

    @Test
    public void deletes(){
        ChatPlatformEntity game = addChatPlatform("Discord");
        manager.delete(game.getId());
        assertNull(manager.get(game.getId()));
    }

    private ChatPlatformEntity addChatPlatform(String name){
        ChatPlatformEntity game = new ChatPlatformEntity(name, null);
        int id = manager.add(game);
        return manager.get(id);
    }

    private List<ChatPlatformEntity> addAllGames(String... names){
        return Arrays.stream(names).map(this::addChatPlatform).collect(Collectors.toList());
    }
}