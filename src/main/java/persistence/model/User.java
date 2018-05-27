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
    private Set<User> sentRequests;
    private Set<User> receivedRequests;
    private Set<Chat> chats;
    private Post post;

    public User(UserEntity entity, Set<Group> groups,
                Set<Game> games, Set<User> friends,
                Set<User> sentRequests, Set<User> receivedRequests,
                Set<Chat> chats, Post post) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.email = entity.getEmail();
        this.admin = entity.isAdmin();
        this.groups = groups;
        this.games = games;
        this.friends = friends;
        this.sentRequests = sentRequests;
        this.receivedRequests = receivedRequests;
        this.post = post;
        this.chats = chats;
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

    public Set<User> getFriends() { return friends; }

    public Set<User> getSentRequests() {
        return sentRequests;
    }

    public Set<User> getReceivedRequests() {
        return receivedRequests;
    }

    public Post getPost() {
        return post;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id &&
                admin == user.admin &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(groups, user.groups) &&
                Objects.equals(games, user.games) &&
                Objects.equals(friends, user.friends) &&
                Objects.equals(sentRequests, user.sentRequests) &&
                Objects.equals(receivedRequests, user.receivedRequests) &&
                Objects.equals(chats, user.chats) &&
                Objects.equals(post, user.post);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, password, email, admin, groups, games, friends, sentRequests, receivedRequests, chats, post);
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
                ", sentRequests=" + sentRequests +
                ", receivedRequests=" + receivedRequests +
                ", chats=" + chats +
                ", post=" + post +
                '}';
    }
}
