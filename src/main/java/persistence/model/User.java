package persistence.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "admin")
    private boolean admin;

    @ManyToMany(mappedBy = "members", cascade = CascadeType.PERSIST)
    private Set<Group> groups;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "owns_game",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private Set<Game> games;

    public User() {
    }

    public User(String username, String password, String email, boolean admin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.admin = admin;
        this.groups = new HashSet<>();
        this.games = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int userId) {
        this.id = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public void removeFromGroup(Group group){
        if(!groups.contains(group))
            return;

        groups.remove(group);
        group.removeMember(this);
    }

    public void addToGroup(Group group){
        if(groups.contains(group))
            return;

        groups.add(group);
        group.addMember(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
