package persistence.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name="game_id", referencedColumnName="id")
    private Game game;

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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Activity(String name, Game game) {
        this.name = name;
        this.game = game;
    }

    public Activity() {
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
