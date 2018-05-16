package persistence.manager.patcher;

import persistence.entity.GameEntity;

/**
 * @author Tomas Perez Molina
 */
public class GamePatcher {
    private String name;
    private String image;

    private GamePatcher(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String game) {
        this.image = game;
    }

    public boolean patchesName(){
        return name != null;
    }

    public boolean patchesImage(){
        return image != null;
    }

    public void patch(GameEntity game){
        if(name != null) game.setName(name);
        if(image != null) game.setImage(image);
    }

    public static class Builder{
        private String name;
        private String image;

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withImage(String image){
            this.image = image;
            return this;
        }

        public GamePatcher build(){
            return new GamePatcher(name, image);
        }
    }

}
