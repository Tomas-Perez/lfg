package api.rest.game.service;

import persistence.entity.GameEntity;
import persistence.manager.GameManager;
import persistence.manager.patcher.GamePatcher;
import persistence.model.Game;
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
public class GameService {

    @Inject
    private GameManager manager;

    @Inject
    private ModelBuilder modelBuilder;

    public List<Game> getAll(){
        return manager.list()
                .stream()
                .map(modelBuilder::buildGame)
                .collect(Collectors.toList());
    }

    public int newGame(String name, String image){
        GameEntity game = new GameEntity(image, name);
        return manager.add(game);
    }

    public Game getGame(int id){
        try {
            return modelBuilder.buildGame(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void wipe(){
        manager.wipe();
    }

    public void deleteGame(int id){
        try {
            manager.delete(id);
        } catch (NoSuchElementException exc){
            throw new NotFoundException();
        }
    }

    public void updateGame(int id, String name, String image){
        GamePatcher patcher = new GamePatcher.Builder()
                .withName(name)
                .withImage(image)
                .build();
        manager.updateGame(id, patcher);
    }
}
