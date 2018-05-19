package persistence.model;

import org.jetbrains.annotations.NotNull;
import persistence.entity.ActivityEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

public class Activity {

    private int id;
    private String name;
    private Game game;

    public Activity(ActivityEntity entity, Game game) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id &&
                Objects.equals(name, activity.name) &&
                Objects.equals(game, activity.game);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, game);
    }
}
