package restapi.group.model;

import persistence.model.User;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class UserJSON {
    private int id;
    private String username;

    public UserJSON(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserJSON() {
    }

    public UserJSON(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserJSON)) return false;
        UserJSON userJSON = (UserJSON) o;
        return id == userJSON.id &&
                Objects.equals(username, userJSON.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "UserJSON{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
