package api.rest.user.model;

import persistence.model.Chat;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ChatJSON {
    private int id;

    public ChatJSON(int id) {
        this.id = id;
    }

    public ChatJSON(Chat chat) {
        this.id = chat.getId();
    }

    public ChatJSON() {
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
        if (!(o instanceof ChatJSON)) return false;
        ChatJSON chatJSON = (ChatJSON) o;
        return id == chatJSON.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatJSON{" +
                "id=" + id + '}';
    }
}
