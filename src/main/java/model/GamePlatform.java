package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_PLATFORM", schema = "PUBLIC", catalog = "PUBLIC")
public class GamePlatform {
    private int gamePlatformId;
    private String name;
    private String image;

    @Id
    @Column(name = "GAME_PLATFORM_ID")
    public int getGamePlatformId() {
        return gamePlatformId;
    }

    public void setGamePlatformId(int gamePlatformId) {
        this.gamePlatformId = gamePlatformId;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "IMAGE")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlatform that = (GamePlatform) o;
        return gamePlatformId == that.gamePlatformId &&
                Objects.equals(name, that.name) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(gamePlatformId, name, image);
    }
}
