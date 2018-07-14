package api.rest.chatPlatform.model;

import persistence.model.ChatPlatform;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ChatPlatformJSON {
    private int id;
    private String name;

    public ChatPlatformJSON(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ChatPlatformJSON() {
    }

    public ChatPlatformJSON(ChatPlatform chatPlatform) {
        this.id = chatPlatform.getId();
        this.name = chatPlatform.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatPlatformJSON)) return false;
        ChatPlatformJSON that = (ChatPlatformJSON) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ChatPlatformJSON{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
