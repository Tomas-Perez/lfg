package api.rest.post.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class FilterPostsJSON {
    private List<PostJSON> posts;
    private String socketPath;

    public FilterPostsJSON(List<PostJSON> posts, String socketPath) {
        this.posts = posts;
        this.socketPath = socketPath;
    }

    public FilterPostsJSON() {
    }

    public List<PostJSON> getPosts() {
        return posts;
    }

    public void setPosts(List<PostJSON> posts) {
        this.posts = posts;
    }

    public String getSocketPath() {
        return socketPath;
    }

    public void setSocketPath(String socketPath) {
        this.socketPath = socketPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterPostsJSON)) return false;
        FilterPostsJSON that = (FilterPostsJSON) o;
        return Objects.equals(posts, that.posts) &&
                Objects.equals(socketPath, that.socketPath);
    }

    @Override
    public int hashCode() {

        return Objects.hash(posts, socketPath);
    }

    @Override
    public String toString() {
        return "FilterPostsJSON{" +
                "posts=" + posts +
                ", socketPath='" + socketPath + '\'' +
                '}';
    }
}
