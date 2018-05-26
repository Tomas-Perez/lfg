package api.rest.user.model;

import persistence.model.Post;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class PostJSON {
    private int id;

    public PostJSON(int id) {
        this.id = id;
    }

    public PostJSON() {
    }

    public PostJSON(Post post) {
        this.id = post.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostJSON)) return false;
        PostJSON postJSON = (PostJSON) o;
        return id == postJSON.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PostJSON{" +
                "id=" + id +
                '}';
    }
}
