package api.rest.group.model;

import persistence.model.User;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class MemberJSON {
    private int id;
    private String username;

    public MemberJSON(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public MemberJSON() {
    }

    public MemberJSON(User user) {
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
        if (!(o instanceof MemberJSON)) return false;
        MemberJSON memberJSON = (MemberJSON) o;
        return id == memberJSON.id &&
                Objects.equals(username, memberJSON.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "MemberJSON{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
