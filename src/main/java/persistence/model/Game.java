package persistence.model;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "game", cascade = {CascadeType.ALL})
    private Set<Activity> activities;

    @ManyToMany(mappedBy = "games", cascade = CascadeType.PERSIST)
    private Set<GamePlatform> gamePlatforms;

    @ManyToMany(mappedBy = "games", cascade = CascadeType.PERSIST)
    private Set<User> owners;

    public Game(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Game() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
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
}
