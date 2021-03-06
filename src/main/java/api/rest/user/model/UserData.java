package api.rest.user.model;

import persistence.model.Post;
import persistence.model.User;

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
    private List<BasicUserData> friends;
    private List<BasicUserData> receivedRequests;
    private List<BasicUserData> sentRequests;
    private List<ChatJSON> chats;
    private PostJSON post;



    public UserData(int id, String username,
                    String email, boolean isAdmin,
                    List<GroupJSON> groups, List<BasicUserData> friends,
                    List<BasicUserData> receivedRequests, List<BasicUserData> sentRequests,
                    List<ChatJSON> chats, PostJSON post)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.groups = groups;
        this.friends = friends;
        this.receivedRequests = receivedRequests;
        this.sentRequests = sentRequests;
        this.chats = chats;
        this.post = post;
    }

    public UserData() {
    }

    public UserData(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isAdmin = user.isAdmin();
        this.groups = user.getGroups().stream().map(GroupJSON::new).collect(Collectors.toList());
        this.friends = user.getFriends().stream().map(BasicUserData::new).collect(Collectors.toList());
        this.sentRequests = user.getSentRequests().stream().map(BasicUserData::new).collect(Collectors.toList());
        this.receivedRequests = user.getReceivedRequests().stream().map(BasicUserData::new).collect(Collectors.toList());
        this.chats = user.getChats().stream().map(ChatJSON::new).collect(Collectors.toList());
        final Post post = user.getPost();
        this.post = post == null? null : new PostJSON(post);
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

    public List<BasicUserData> getFriends() {
        return friends;
    }

    public void setFriends(List<BasicUserData> friends) {
        this.friends = friends;
    }

    public List<BasicUserData> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(List<BasicUserData> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    public List<BasicUserData> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(List<BasicUserData> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public PostJSON getPost() {
        return post;
    }

    public void setPost(PostJSON post) {
        this.post = post;
    }

    public List<ChatJSON> getChats() {
        return chats;
    }

    public void setChats(List<ChatJSON> chats) {
        this.chats = chats;
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
                Objects.equals(groups, userData.groups) &&
                Objects.equals(friends, userData.friends) &&
                Objects.equals(receivedRequests, userData.receivedRequests) &&
                Objects.equals(sentRequests, userData.sentRequests) &&
                Objects.equals(chats, userData.chats) &&
                Objects.equals(post, userData.post);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username, email, isAdmin, groups, friends, receivedRequests, sentRequests, chats, post);
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", groups=" + groups +
                ", friends=" + friends +
                ", receivedRequests=" + receivedRequests +
                ", sentRequests=" + sentRequests +
                ", chats=" + chats +
                ", post=" + post +
                '}';
    }
}
