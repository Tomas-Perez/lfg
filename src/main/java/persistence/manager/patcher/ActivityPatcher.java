package persistence.manager.patcher;

import persistence.entity.ActivityEntity;


/**
 * @author Tomas Perez Molina
 */
public class ActivityPatcher {
    private String name;
    private Integer game;

    private ActivityPatcher(String name, Integer game) {
        this.name = name;
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGame() {
        return game;
    }

    public void setGame(Integer game) {
        this.game = game;
    }

    public boolean patchesName(){
        return name != null;
    }

    public boolean patchesGame(){
        return game != null;
    }

    public void patch(ActivityEntity activity){
        if(name != null) activity.setName(name);
        if(game != null) activity.setGameId(game);
    }

    public static class Builder{
        private String name;
        private Integer game;

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withGame(Integer game){
            this.game = game;
            return this;
        }

        public ActivityPatcher build(){
            return new ActivityPatcher(name, game);
        }
    }
}
