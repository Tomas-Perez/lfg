package api.rest.user.model;

import persistence.model.User;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class BasicUserData {
    private int id;
    private String username;

    public BasicUserData(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public BasicUserData() {
    }

    public BasicUserData(User user) {
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
        if (!(o instanceof BasicUserData)) return false;
        BasicUserData that = (BasicUserData) o;
        return id == that.id &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "BasicUserData{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
