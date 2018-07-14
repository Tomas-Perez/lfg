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
    private GroupManager groupManager;

    @Inject
    public ChatManager(EntityManager manager, UserManager userManager, GroupManager groupManager) {
        super(manager);
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    public ChatManager() {}

    @Override
    public int add(ChatEntity chat) {
        persist(chat);
        return chat.getId();
    }

    public int addPrivateChat(int user1ID, int user2ID){
        ChatEntity chat = new ChatEntity();
        Integer chatID = getPrivateChat(user1ID, user2ID);
        if(chatID == null) {
            chatID = add(chat);
            addMemberToChat(chatID, user1ID, true);
            addMemberToChat(chatID, user2ID, false);
        } else {
            setOpenChat(chatID, user1ID, true);
        }
        return chatID;
    }

    public int addGroupChat(int groupID){
        groupManager.checkExistence(groupID);
        ChatEntity chat = new ChatEntity(groupID);
        int chatID = add(chat);
        groupManager.getGroupMembers(groupID)
                .forEach(id -> addMemberToChat(chatID, id, true));
        return chatID;
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
        checkAddMember(chatID, userID);
        ChatMemberEntity chatMemberEntity = new ChatMemberEntity(chatID, userID);
        persist(chatMemberEntity);
    }

    private void addMemberToChat(int chatID, int userID, boolean open){
        checkAddMember(chatID, userID);
        ChatMemberEntity chatMemberEntity = new ChatMemberEntity(chatID, userID);
        chatMemberEntity.setOpenChat(open);
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

        final int chatSize = getChatMembers(chatID).size();

        if(chatSize == 0 || (chatSize == 1 && isChatOfType(chatID, ChatEntity.ChatType.PRIVATE))){
            delete(chatID);
        }
    }

    private void checkAddMember(int chatID, int userID){
        userManager.checkExistence(userID);
        checkExistence(chatID);
        if(isUserInChat(chatID, userID)){
            final String constraintName =
                    String.format("User with id %d is already in the chat with id %d", userID, chatID);
            throw new ConstraintException(constraintName);
        }
    }

    private boolean isUserInChat(int chatID, int userID){
        return manager.createQuery(
                "SELECT 1 FROM ChatMemberEntity " +
                        "WHERE EXISTS ( " +
                        "SELECT 1 FROM ChatMemberEntity M " +
                        "WHERE M.memberId = :userID AND M.chatId = :chatID)")
                .setParameter("userID", userID)
                .setParameter("chatID", chatID)
                .getResultList().size() > 0;
    }

    private void setOpenChat(int chatID, int userID, boolean open){
        ChatMemberEntityPK key = new ChatMemberEntityPK(chatID, userID);
        ChatMemberEntity chatMemberEntity = manager.find(ChatMemberEntity.class, key);
        if(chatMemberEntity == null) throw new NoSuchElementException();

        EntityTransaction tx = manager.getTransaction();
        try {
            tx.begin();
            chatMemberEntity.setOpenChat(open);
            tx.commit();
        } catch (Exception e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void openChat(int chatID, int userID){
        setOpenChat(chatID, userID, true);
    }

    public void closeChat(int chatID, int userID){
        setOpenChat(chatID, userID, false);
    }

    @SuppressWarnings("unchecked")
    private Integer getPrivateChat(int user1ID, int user2ID){
        return (Integer) manager.createQuery("" +
                "SELECT C.id FROM ChatMemberEntity M\n" +
                "JOIN ChatEntity C on M.chatId = C.id\n" +
                "JOIN ChatMemberEntity M2 on M2.chatId = C.id AND M2.memberId != M.memberId\n" +
                "WHERE C.groupId = NULL AND M.memberId = :user1ID AND M2.memberId = :user2ID")
                .setParameter("user1ID", user1ID)
                .setParameter("user2ID", user2ID)
                .getResultList().stream().findFirst().orElse(null);
    }

    private boolean singleMemberChat(int chatID){
        return getChatMembers(chatID).size() <= 1;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getChatMembers(int chatID){
        return manager.createQuery("SELECT M.memberId " +
                "FROM ChatMemberEntity M " +
                "WHERE M.chatId = :chatID")
                .setParameter("chatID", chatID)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getClosedChatMembers(int chatID){
        return manager.createQuery("SELECT M.memberId " +
                "FROM ChatMemberEntity M " +
                "WHERE M.chatId = :chatID AND M.openChat = false")
                .setParameter("chatID", chatID)
                .getResultList();
    }

    public int sendMessage(int chatID, int fromID, String message, LocalDateTime date){
        if(!isUserInChat(chatID, fromID)){
            final String format =
                    String.format("User with id %d is not a member of the chat with id %d", fromID, chatID);
            throw new ConstraintException(format);
        }
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
                "WHERE M.chatId = :chatID ORDER BY M.date ASC")
                .setParameter("chatID", chatID)
                .getResultList();
    }

    private boolean isChatOfType(int chatID, ChatEntity.ChatType chatType){
        return get(chatID).getType() == chatType;
    }
}
