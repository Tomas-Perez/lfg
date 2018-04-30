package persistence.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "activity", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "game_id"}))
public class Activity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
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
        if(sameAsFormer(game))
            return;

        Game oldGame = this.game;
        this.game = game;

        if(oldGame != null)
            oldGame.removeActivity(this);

        if(game != null)
            game.addActivity(this);
    }

    private boolean sameAsFormer(Game newGame){
        return game == null ? newGame == null : game.equals(newGame);
    }

    public Activity(String name, Game game) {
        this.name = name;
        setGame(game);
    }

    public Activity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", game=" + game.getName() +
                '}';
    }
}
