package restapi.game.model;

import persistence.model.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class GameJSON {
    private int id;
    private String name;
    private List<ActivityJSON> activities;

    public GameJSON(int id, String name) {
        this.id = id;
        this.name = name;
        this.activities = new ArrayList<>();
    }

    public GameJSON() {
    }

    public GameJSON(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.activities = game.getActivities()
                .stream()
                .map(ActivityJSON::new)
                .collect(Collectors.toList());
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

    public List<ActivityJSON> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityJSON> activities) {
        this.activities = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameJSON)) return false;
        GameJSON gameJSON = (GameJSON) o;
        return id == gameJSON.id &&
                Objects.equals(name, gameJSON.name) &&
                Objects.equals(activities, gameJSON.activities);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, activities);
    }

    @Override
    public String toString() {
        return "GameJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", activities=" + activities +
                '}';
    }
}
