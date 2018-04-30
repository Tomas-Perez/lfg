package restapi.activity.model;

import persistence.model.Activity;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ActivityJSON {
    private int id;
    private String name;
    private GameJSON game;

    public ActivityJSON(int id, String name, GameJSON game) {
        this.id = id;
        this.name = name;
        this.game = game;
    }

    public ActivityJSON(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.game = new GameJSON(activity.getGame());
    }

    public ActivityJSON() {
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

    public GameJSON getGame() {
        return game;
    }

    public void setGame(GameJSON game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityJSON)) return false;
        ActivityJSON that = (ActivityJSON) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, game);
    }

    @Override
    public String toString() {
        return "ActivityJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", game=" + game +
                '}';
    }
}
