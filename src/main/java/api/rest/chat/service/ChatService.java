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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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

    public int newChat(List<Integer> members){
        ChatEntity chatEntity = new ChatEntity();
        final int chatID = chatManager.addGroupChat(chatEntity, members);
        newChatEvent.fire(new ChatEvent(chatID, new HashSet<>(members)));
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

        newChatEvent.fire(new ChatEvent(id, new HashSet<>(userID)));
    }

    public void removeMember(int id, int userID){
        try {
            chatManager.removeMemberFromChat(id, userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }

        deleteChatEvent.fire(new ChatEvent(id, new HashSet<>(userID)));
    }
}
