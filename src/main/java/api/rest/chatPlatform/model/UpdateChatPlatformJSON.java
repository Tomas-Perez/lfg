package api.rest.chatPlatform.model;

/**
 * @author Tomas Perez Molina
 */
public class UpdateChatPlatformJSON {
    private String name;
    private String image;

    public UpdateChatPlatformJSON() {
    }

    public UpdateChatPlatformJSON(String name, String image) {
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

    public void setImage(String image) {
        this.image = image;
    }
}
