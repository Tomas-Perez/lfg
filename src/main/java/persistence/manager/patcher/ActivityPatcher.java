package persistence.manager.patcher;

import persistence.model.Activity;
import persistence.model.Game;

/**
 * @author Tomas Perez Molina
 */
public class ActivityPatcher {
    private String name;
    private Game game;

    private ActivityPatcher(String name, Game game) {
        this.name = name;
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean patchesName(){
        return name != null;
    }

    public boolean patchesGame(){
        return game != null;
    }

    public void patch(Activity activity){
        System.out.println("BEFORE PATCH: " + activity);
        if(name != null) activity.setName(name);
        if(game != null) activity.setGame(game);
        System.out.println("AFTER PATCH: " + activity);
    }

    public static class Builder{
        private String name;
        private Game game;

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withGame(Game game){
            this.game = game;
            return this;
        }

        public ActivityPatcher build(){
            return new ActivityPatcher(name, game);
        }
    }
}
