package persistence.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "activity", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "game_id"}))
public class Activity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name="game_id", referencedColumnName="id")
    private Game game;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.ALL})
    private Set<Group> groups;

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

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group){
        if(groups.contains(group))
            return;

        groups.add(group);
        group.setActivity(this);
    }

    public void removeGroup(Group group){
        if(!groups.contains(group))
            return;

        groups.remove(group);
        group.setActivity(null);
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

    public void destroy(){
        game.removeActivity(this);
        new HashSet<>(groups).forEach(Group::destroy);
    }

    private boolean sameAsFormer(Game newGame){
        return game == null ? newGame == null : game.equals(newGame);
    }

    public Activity(int id, String name, Game game) {
        this.id = id;
        this.name = name;
        this.groups = new HashSet<>();
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
