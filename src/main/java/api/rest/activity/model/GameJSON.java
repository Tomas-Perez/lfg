package api.rest.activity.model;

import persistence.model.Game;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GameJSON {
    private int id;
    private String name;

    public GameJSON(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GameJSON() {
    }

    public GameJSON(Game game) {
        this.id = game.getId();
        this.name = game.getName();
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
        if (!(o instanceof GameJSON)) return false;
        GameJSON gameJSON = (GameJSON) o;
        return id == gameJSON.id &&
                Objects.equals(name, gameJSON.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "GameJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
