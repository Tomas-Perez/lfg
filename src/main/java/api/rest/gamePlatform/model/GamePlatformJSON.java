package api.rest.gamePlatform.model;

import persistence.model.GamePlatform;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GamePlatformJSON {
    private int id;
    private String name;

    public GamePlatformJSON(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GamePlatformJSON() {
    }

    public GamePlatformJSON(GamePlatform gamePlatform) {
        this.id = gamePlatform.getId();
        this.name = gamePlatform.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlatformJSON)) return false;
        GamePlatformJSON that = (GamePlatformJSON) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "GamePlatformJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
