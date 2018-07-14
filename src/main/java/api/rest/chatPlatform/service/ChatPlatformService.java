package api.rest.chatPlatform.service;

import persistence.entity.ChatPlatformEntity;
import persistence.manager.ChatPlatformManager;
import persistence.manager.patcher.ChatPlatformPatcher;
import persistence.model.ChatPlatform;
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
public class ChatPlatformService {

    @Inject
    private ChatPlatformManager manager;

    @Inject
    private ModelBuilder modelBuilder;

    public List<ChatPlatform> getAll(){
        return manager.list()
                .stream()
                .map(modelBuilder::buildChatPlatform)
                .collect(Collectors.toList());
    }

    public int newChatPlatform(String name, String image){
        ChatPlatformEntity chatPlatformEntity = new ChatPlatformEntity(name, image);
        return manager.add(chatPlatformEntity);
    }

    public ChatPlatform getChatPlatform(int id){
        try {
            return modelBuilder.buildChatPlatform(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void wipe(){
        manager.wipe();
    }

    public void deleteChatPlatform(int id){
        try {
            manager.delete(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void updateChatPlatform(int id, String name, String image){
        ChatPlatformPatcher patcher = new ChatPlatformPatcher.Builder()
                .withName(name)
                .withImage(image)
                .build();
        manager.updateChatPlatform(id, patcher);
    }
}
