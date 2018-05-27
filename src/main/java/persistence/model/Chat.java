package persistence.model;


import persistence.entity.ChatEntity;

import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class Chat {
    private int id;
    private Set<User> members;
    private Set<Message> messages;

    public Chat(ChatEntity entity, Set<User> members, Set<Message> messages) {
        this.id = entity.getId();
        this.members = members;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public Set<User> getMembers() {
        return members;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat)) return false;
        Chat chat = (Chat) o;
        return id == chat.id &&
                Objects.equals(members, chat.members) &&
                Objects.equals(messages, chat.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, members, messages);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
