package persistence.model;

import javax.persistence.*;
import java.util.HashSet;
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
        this.activities = new HashSet<>();
        this.gamePlatforms = new HashSet<>();
        this.owners = new HashSet<>();
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

    public Set<GamePlatform> getGamePlatforms() {
        return gamePlatforms;
    }

    public void setGamePlatforms(Set<GamePlatform> gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
    }

    public Set<User> getOwners() {
        return owners;
    }

    public void setOwners(Set<User> owners) {
        this.owners = owners;
    }

    public void addActivity(Activity activity){
        if(activities.contains(activity))
            return;

        activities.add(activity);
        activity.setGame(this);
    }

    public void removeActivity(Activity activity){
        if(!activities.contains(activity))
            return;

        activities.remove(activity);
        activity.setGame(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id == game.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
