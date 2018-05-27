package api.rest.chat.service;

import persistence.entity.ChatEntity;
import persistence.manager.ChatManager;
import persistence.model.Chat;
import persistence.model.ModelBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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

    public List<Chat> getAll(){
        return chatManager.list()
                .stream()
                .map(modelBuilder::buildChat)
                .collect(Collectors.toList());
    }

    public int newChat(List<Integer> members){
        ChatEntity chatEntity = new ChatEntity();
        return chatManager.addGroupChat(chatEntity, members);
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
        try {
            chatManager.delete(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void addMember(int id, int userID){
        try {
            chatManager.addMemberToChat(id, userID);
        }  catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void removeMember(int id, int userID){
        try {
            chatManager.removeMemberFromChat(id, userID);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }
}
