package persistence.model;

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

    public User(int id, String username, String password, String email, boolean admin, Set<Group> groups, Set<Game> games) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.admin = admin;
        this.groups = groups;
        this.games = games;
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
                Objects.equals(games, user.games);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, password, email, admin, groups, games);
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
                '}';
    }
}
