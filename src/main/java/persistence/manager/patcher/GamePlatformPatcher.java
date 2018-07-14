package persistence.manager.patcher;

import persistence.entity.GamePlatformEntity;

/**
 * @author Tomas Perez Molina
 */
public class GamePlatformPatcher {
    private String name;
    private String image;

    private GamePlatformPatcher(String name, String image) {
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

    public void patch(GamePlatformEntity gamePlatform){
        if(name != null) gamePlatform.setName(name);
        if(image != null) gamePlatform.setImage(image);
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

        public GamePlatformPatcher build(){
            return new GamePlatformPatcher(name, image);
        }
    }

}
