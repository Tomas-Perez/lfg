package persistence.manager.patcher;

import persistence.entity.ChatPlatformEntity;

/**
 * @author Tomas Perez Molina
 */
public class ChatPlatformPatcher {
    private String name;
    private String image;

    private ChatPlatformPatcher(String name, String image) {
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

    public void patch(ChatPlatformEntity chatPlatform){
        if(name != null) chatPlatform.setName(name);
        if(image != null) chatPlatform.setImage(image);
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

        public ChatPlatformPatcher build(){
            return new ChatPlatformPatcher(name, image);
        }
    }

}
