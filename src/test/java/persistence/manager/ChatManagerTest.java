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
import persistence.entity.ChatEntity;
import persistence.entity.ChatMessageEntity;
import persistence.entity.UserEntity;
import persistence.manager.exception.ConstraintException;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Tomas Perez Molina
 */

@RunWith(Arquillian.class)
public class ChatManagerTest {
    @Inject
    private EntityManagerProducer entityManagerProducer;

    @Inject
    private UserManager userManager;

    @Inject
    private ChatManager chatManager;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(EmbeddedGradleImporter.class)
                .forThisProjectDirectory()
                .importBuildOutput().as(WebArchive.class);
    }

    @Before
    @After
    public void setup(){
        chatManager.wipe();
        userManager.wipe();
    }

    @Test
    public void shouldCreateManager(){
        assertNotNull(userManager);
        assertNotNull(chatManager);
    }

    @Test
    public void addChat(){
        ChatEntity chat = new ChatEntity();
        int id = chatManager.add(chat);
        ChatEntity actual = chatManager.get(id);
        assertNotNull(actual);
        assertThat(actual.getId(), is(id));
    }

    @Test
    public void addPrivateChatTwice(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id1 = chatManager.addPrivateChat(chat, user1ID, user2ID);
        int id2 = chatManager.addPrivateChat(chat, user1ID, user2ID);

        assertThat(id1, is(id2));
    }

    @Test
    public void addPrivateChat(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        List<Integer> memberIDs = chatManager.getChatMembers(id);
        List<Integer> expectedIDs = Arrays.asList(user1ID, user2ID);

        assertThat(new HashSet<>(memberIDs), is(new HashSet<>(expectedIDs)));
    }

    @Test
    public void addGroupChat(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);
        UserEntity user3 = new UserEntity(false, "email3", "password3", "username3");
        int user3ID = userManager.add(user3);
        UserEntity user4 = new UserEntity(false, "email4", "password4", "username4");
        int user4ID = userManager.add(user4);

        List<Integer> expectedIDs = Arrays.asList(user1ID, user2ID, user3ID, user4ID);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addGroupChat(chat, expectedIDs);

        List<Integer> memberIDs = chatManager.getChatMembers(id);

        assertThat(new HashSet<>(memberIDs), is(new HashSet<>(expectedIDs)));
    }

    @Test
    public void list(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id1 = chatManager.addPrivateChat(chat, user1ID, user2ID);

        UserEntity user3 = new UserEntity(false, "email3", "password3", "username3");
        int user3ID = userManager.add(user3);
        UserEntity user4 = new UserEntity(false, "email4", "password4", "username4");
        int user4ID = userManager.add(user4);

        List<Integer> memberIDs = Arrays.asList(user1ID, user2ID, user3ID, user4ID);
        ChatEntity chat2 = new ChatEntity();
        int id2 = chatManager.addGroupChat(chat2, memberIDs);

        List<Integer> expectedIDs = Arrays.asList(id1, id2);
        List<Integer> actualIDs = chatManager.list();

        assertThat(new HashSet<>(actualIDs), is(new HashSet<>(expectedIDs)));
    }

    @Test
    public void deletes(){
        ChatEntity chat = new ChatEntity();
        int id = chatManager.add(chat);
        chatManager.delete(id);
        assertNull(chatManager.get(id));
    }

    @Test
    public void canDeleteMember(){
        EntityManager userEM = entityManagerProducer.createEntityManager();
        EntityManager chatEM = entityManagerProducer.createEntityManager();
        FriendHelperManager friendHelperManager = new FriendHelperManager();
        UserManager userManager = new UserManager(userEM, friendHelperManager);
        ChatManager chatManager = new ChatManager(chatEM, userManager);

        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        userManager.delete(user1ID);

        entityManagerProducer.closeEntityManager(userEM);
        EntityManager userEM2 = entityManagerProducer.createEntityManager();
        UserManager userManager2 = new UserManager(userEM2, friendHelperManager);
        ChatManager chatManager2 = new ChatManager(chatEM, userManager2);

        List<Integer> actualMembers = chatManager2.getChatMembers(id);
        List<Integer> expectedMembers = Collections.singletonList(user2ID);

        assertThat(new HashSet<>(actualMembers), is(new HashSet<>(expectedMembers)));

        entityManagerProducer.closeEntityManager(userEM2);
        entityManagerProducer.closeEntityManager(chatEM);
    }

    @Test
    public void addsChatToMember(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        Set<Integer> member1Chats = userManager.getUserChats(user1ID);
        Set<Integer> expectedChats = new HashSet<>(Collections.singletonList(id));

        assertThat(member1Chats, is(expectedChats));
    }

    @Test
    public void removeMember(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);
        UserEntity user3 = new UserEntity(false, "email3", "password3", "username3");
        int user3ID = userManager.add(user3);
        UserEntity user4 = new UserEntity(false, "email4", "password4", "username4");
        int user4ID = userManager.add(user4);

        List<Integer> initialMemberIDs = Arrays.asList(user1ID, user2ID, user3ID, user4ID);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addGroupChat(chat, initialMemberIDs);

        chatManager.removeMemberFromChat(id, user1ID);

        List<Integer> memberIDs = chatManager.getChatMembers(id);
        List<Integer> expectedIDs = Arrays.asList(user2ID, user3ID, user4ID);

        assertThat(new HashSet<>(memberIDs), is(new HashSet<>(expectedIDs)));
    }

    @Test
    public void removeMemberAndDeleteChat(){
        EntityManager userEM = entityManagerProducer.createEntityManager();
        EntityManager chatEM = entityManagerProducer.createEntityManager();
        FriendHelperManager friendHelperManager = new FriendHelperManager();
        UserManager userManager = new UserManager(userEM, friendHelperManager);
        ChatManager chatManager = new ChatManager(chatEM, userManager);


        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        chatManager.removeMemberFromChat(id, user1ID);

        entityManagerProducer.closeEntityManager(chatEM);
        EntityManager chatEM2 = entityManagerProducer.createEntityManager();
        ChatManager chatManager2 = new ChatManager(chatEM2, userManager);

        assertNull(chatManager2.get(id));
        assertThat(userManager.getUserChats(user1ID), is(new HashSet<>()));
        assertThat(userManager.getUserChats(user2ID), is(new HashSet<>()));

        entityManagerProducer.closeEntityManager(userEM);
        entityManagerProducer.closeEntityManager(chatEM2);
    }

    @Test(expected = ConstraintException.class)
    public void repeatAddExc(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        chatManager.addMemberToChat(id, user1ID);
    }

    @Test
    public void sendMessage(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        final LocalDateTime now = LocalDateTime.now();
        final String msgString = "Hello";
        int msgID = chatManager.sendMessage(id, user1ID, msgString, now);

        final ChatMessageEntity message = chatManager.getMessage(msgID);
        assertNotNull(message);
        assertThat(message.getSenderId(), is(user1ID));
        assertThat(message.getMessage(), is(msgString));
        assertThat(message.getDate(), is(now));
    }

    @Test
    public void deleteMessage(){
        EntityManager userEM = entityManagerProducer.createEntityManager();
        EntityManager chatEM = entityManagerProducer.createEntityManager();
        FriendHelperManager friendHelperManager = new FriendHelperManager();
        UserManager userManager = new UserManager(userEM, friendHelperManager);
        ChatManager chatManager = new ChatManager(chatEM, userManager);


        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        final LocalDateTime now = LocalDateTime.now();
        final String msgString = "Hello";
        int msgID = chatManager.sendMessage(id, user1ID, msgString, now);
        chatManager.deleteMessage(msgID);

        entityManagerProducer.closeEntityManager(chatEM);
        EntityManager chatEM2 = entityManagerProducer.createEntityManager();
        ChatManager chatManager2 = new ChatManager(chatEM2, userManager);

        assertNull(chatManager2.getMessage(msgID));

        entityManagerProducer.closeEntityManager(userEM);
        entityManagerProducer.closeEntityManager(chatEM2);
    }

    @Test
    public void listMessages(){
        UserEntity user1 = new UserEntity(false, "email1", "password1", "username1");
        int user1ID = userManager.add(user1);
        UserEntity user2 = new UserEntity(false, "email2", "password2", "username2");
        int user2ID = userManager.add(user2);

        ChatEntity chat = new ChatEntity();
        int id = chatManager.addPrivateChat(chat, user1ID, user2ID);

        final LocalDateTime now = LocalDateTime.now();
        final String msgString = "Hello";
        int msgID1 = chatManager.sendMessage(id, user1ID, msgString, now);

        final LocalDateTime now2 = LocalDateTime.now();
        final String msgString2 = "Hello2";
        int msgID2 = chatManager.sendMessage(id, user1ID, msgString2, now2);

        final LocalDateTime now3 = LocalDateTime.now();
        final String msgString3 = "Hello";
        int msgID3 = chatManager.sendMessage(id, user1ID, msgString3, now3);

        List<Integer> expected = Arrays.asList(msgID1, msgID2, msgID3);
        List<Integer> actual = chatManager.getChatMessages(id);

        assertThat(new HashSet<>(actual), is(new HashSet<>(expected)));
    }
}