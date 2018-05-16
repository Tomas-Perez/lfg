package persistence.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

public class Game {

    private int id;
    private String name;
    private String image;
    private Set<Activity> activities;

    public Game(int id, String name, String image, Set<Activity> activities) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.activities = activities;
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

    public Set<Activity> getActivities() {
        return activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id == game.id &&
                Objects.equals(name, game.name) &&
                Objects.equals(image, game.image) &&
                Objects.equals(activities, game.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image, activities);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", activities=" + activities +
                '}';
    }
}
