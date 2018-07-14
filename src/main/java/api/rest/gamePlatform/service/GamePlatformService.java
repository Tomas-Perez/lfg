package api.rest.gamePlatform.service;

import persistence.entity.GamePlatformEntity;
import persistence.manager.GamePlatformManager;
import persistence.manager.patcher.GamePlatformPatcher;
import persistence.model.GamePlatform;
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
public class GamePlatformService {

    @Inject
    private GamePlatformManager manager;

    @Inject
    private ModelBuilder modelBuilder;

    public List<GamePlatform> getAll(){
        return manager.list()
                .stream()
                .map(modelBuilder::buildGamePlatform)
                .collect(Collectors.toList());
    }

    public int newGamePlatform(String name, String image){
        GamePlatformEntity gamePlatformEntity = new GamePlatformEntity(name, image);
        return manager.add(gamePlatformEntity);
    }

    public GamePlatform getGamePlatform(int id){
        try {
            return modelBuilder.buildGamePlatform(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void wipe(){
        manager.wipe();
    }

    public void deleteGamePlatform(int id){
        try {
            manager.delete(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void updateGamePlatform(int id, String name, String image){
        GamePlatformPatcher patcher = new GamePlatformPatcher.Builder()
                .withName(name)
                .withImage(image)
                .build();
        manager.updateGamePlatform(id, patcher);
    }
}
