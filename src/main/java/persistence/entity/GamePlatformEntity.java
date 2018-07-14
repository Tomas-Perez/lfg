package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_PLATFORM", schema = "PUBLIC", catalog = "PUBLIC")
public class GamePlatformEntity {
    private int id;
    private String image;
    private String name;

    public GamePlatformEntity(int id, String name, String image) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public GamePlatformEntity() {
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "IMAGE", nullable = true, length = 255)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Basic
    @Column(name = "NAME", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlatformEntity that = (GamePlatformEntity) o;
        return id == that.id &&
                Objects.equals(image, that.image) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, image, name);
    }
}
