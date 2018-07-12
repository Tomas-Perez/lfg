package api.rest.chat.service;

import api.common.event.chat.DeleteChat;
import api.common.event.chat.NewChat;
import api.common.event.chat.ChatEvent;
import persistence.entity.ChatEntity;
import persistence.manager.ChatManager;
import persistence.model.Chat;
import persistence.model.ModelBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */

@ApplicationScoped
public class ChatService {

    @Inject
    private ChatManager chatManager;

    @Inject
    private ModelBuilder modelBuilder;

    @Inject
    @NewChat
    private Event<ChatEvent> newChatEvent;

    @Inject
    @DeleteChat
    private Event<ChatEvent> deleteChatEvent;

    public List<Chat> getAll(){
        return chatManager.list()
                .stream()
                .map(modelBuilder::buildChat)
                .collect(Collectors.toList());
    }

    public int newGroupChat(int groupID){
        final int chatID = chatManager.addGroupChat(groupID);
//        newChatEvent.fire(new ChatEvent(chatID, new HashSet<>(members)));
        return chatID;
    }

    public int newPrivateChat(int member1, int member2){
        final int chatID = chatManager.addPrivateChat(member1, member2);
//        newChatEvent.fire(new ChatEvent(chatID, new HashSet<>(Arrays.asList(member1, member2))));
        return chatID;
    }

    public Chat getChat(int id){
        try {
            return modelBuilder.buildChat(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void wipe(){
        chatManager.wipe();
    }

    public void deleteChat(int id){
        List<Integer> members = chatManager.getChatMembers(id);
        try {
            chatManager.delete(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }

        deleteChatEvent.fire(new ChatEvent(id, new HashSet<>(members)));
    }

    public void addMember(int id, int userID){
        try {
            chatManager.addMemberToChat(id, userID);
        }  catch (NoSuchElementException exc){
            throw new NotFoundException();
        }

        newChatEvent.fire(new ChatEvent(id, new HashSet<>(Collections.singletonList(userID))));
    }

    public void removeMember(int id, int userID){
        try {
            chatManager.removeMemberFromChat(id, userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }

        deleteChatEvent.fire(new ChatEvent(id, new HashSet<>(Collections.singletonList(userID))));
    }

    public void closeChat(int id, int userID){
        try{
            chatManager.closeChat(id, userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }
}
