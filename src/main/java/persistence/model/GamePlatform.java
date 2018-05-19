package persistence.model;

import persistence.entity.GamePlatformEntity;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */

public class GamePlatform {
    private int id;
    private String name;
    private String image;

    public GamePlatform(GamePlatformEntity entity) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlatform that = (GamePlatform) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, image);
    }

    @Override
    public String toString() {
        return "GamePlatform{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
