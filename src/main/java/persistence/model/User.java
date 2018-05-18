package persistence.model;

import persistence.entity.UserEntity;

import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */

public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private boolean admin;
    private Set<Group> groups;
    private Set<Game> games;
    private Set<User> friends;

    public User(UserEntity entity, Set<Group> groups, Set<Game> games, Set<User> friends) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.email = entity.getEmail();
        this.admin = entity.isAdmin();
        this.groups = groups;
        this.games = games;
        this.friends = friends;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public Set<Game> getGames() {
        return games;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                admin == user.admin &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(groups, user.groups) &&
                Objects.equals(games, user.games) &&
                Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, password, email, admin, groups, games, friends);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", admin=" + admin +
                ", groups=" + groups +
                ", games=" + games +
                ", friends=" + friends +
                '}';
    }
}
