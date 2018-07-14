package persistence.model;

import persistence.entity.ChatPlatformEntity;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */

public class ChatPlatform {
    private int id;
    private String name;
    private String image;

    public ChatPlatform(ChatPlatformEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.image = entity.getImage();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ChatPlatform{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPlatform that = (ChatPlatform) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, image);
    }
}
