package restapi.user.model;

import persistence.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class UserData {
    private int id;
    private String username;
    private String email;
    private boolean isAdmin;
    private List<GroupJSON> groups;



    public UserData(int id, String username, String email, boolean isAdmin, List<GroupJSON> groups) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.groups = groups;
    }

    public UserData() {
    }

    public UserData(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isAdmin = user.isAdmin();
        this.groups = user.getGroups().stream().map(GroupJSON::new).collect(Collectors.toList());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<GroupJSON> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupJSON> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;
        UserData userData = (UserData) o;
        return id == userData.id &&
                isAdmin == userData.isAdmin &&
                Objects.equals(username, userData.username) &&
                Objects.equals(email, userData.email) &&
                new HashSet<>(groups).equals(new HashSet<>(userData.groups));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, isAdmin, groups);
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", groups=" + groups +
                '}';
    }
}
