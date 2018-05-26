package api.rest.game.model;

import persistence.model.Activity;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ActivityJSON {
    private int id;
    private String name;

    public ActivityJSON(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ActivityJSON() {
    }

    public ActivityJSON(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
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
        if (!(o instanceof ActivityJSON)) return false;
        ActivityJSON that = (ActivityJSON) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ActivityJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
