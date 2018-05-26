package persistence.manager;

import persistence.entity.ChatEntity;
import persistence.entity.ChatMemberEntity;
import persistence.entity.ChatMemberEntityPK;
import persistence.entity.ChatMessageEntity;
import persistence.manager.exception.ConstraintException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class ChatManager extends Manager<ChatEntity> {
    private UserManager userManager;

    @Inject
    public ChatManager(EntityManager manager, UserManager userManager) {
        super(manager);
        this.userManager = userManager;
    }

    public ChatManager() {}

    @Override
    public int add(ChatEntity chat) {
        persist(chat);
        return chat.getId();
    }

    @Override
    public ChatEntity get(int chatID) {
        return manager.find(ChatEntity.class, chatID);
    }

    @Override
    public void delete(int chatID) {
        delete(ChatEntity.class, chatID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> list() {
        return manager.createQuery("SELECT C.id FROM ChatEntity C").getResultList();
    }

    @Override
    public void checkExistence(int chatID) {
        if(!exists(chatID)){
            throw new ConstraintException(String.format("Chat with id: %d does not exist", chatID));
        }
    }

    public void addMemberToChat(int chatID, int userID){
        userManager.checkExistence(userID);
        checkExistence(chatID);
        ChatMemberEntity chatMemberEntity = new ChatMemberEntity(chatID, userID);
        persist(chatMemberEntity);
    }

    public void removeMemberFromChat(int chatID, int userID){
        EntityTransaction tx = manager.getTransaction();
        ChatMemberEntityPK key = new ChatMemberEntityPK(chatID, userID);
        try {
            tx.begin();
            ChatMemberEntity chatMemberEntity = manager.find(ChatMemberEntity.class, key);
            manager.remove(chatMemberEntity);
            tx.commit();
        } catch (IllegalArgumentException exc){
            if (tx!=null) tx.rollback();
            throw new NoSuchElementException();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }

        //TODO delete chat with only one member left.
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getChatMembers(int chatID){
        return manager.createQuery("SELECT M.memberId " +
                "FROM ChatMemberEntity M " +
                "WHERE M.chatId = :chatID")
                .setParameter("chatID", chatID)
                .getResultList();
    }

    public int sendMessage(int chatID, int fromID, String message, LocalDateTime date){
        ChatMessageEntity messageEntity = new ChatMessageEntity(chatID, fromID, message, date);
        persist(messageEntity);
        return messageEntity.getId();
    }

    public void deleteMessage(int messageID){
        delete(ChatMessageEntity.class, messageID);
    }

    public ChatMessageEntity getMessage(int messageID){
        return manager.find(ChatMessageEntity.class, messageID);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getChatMessages(int chatID){
        return manager.createQuery("SELECT M.id " +
                "FROM ChatMessageEntity M " +
                "WHERE M.chatId = :chatID")
                .setParameter("chatID", chatID)
                .getResultList();
    }
}
