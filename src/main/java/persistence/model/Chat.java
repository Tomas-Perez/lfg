package persistence.model;


import persistence.entity.ChatEntity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class Chat {
    private int id;
    private ChatEntity.ChatType type;
    private Set<User> members;
    private List<Message> messages;

    public Chat(ChatEntity entity, Set<User> members, List<Message> messages) {
        this.id = entity.getId();
        this.type = entity.getType();
        this.members = members;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public Set<User> getMembers() {
        return members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ChatEntity.ChatType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", type=" + type +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chat)) return false;
        Chat chat = (Chat) o;
        return id == chat.id &&
                type == chat.type &&
                Objects.equals(members, chat.members) &&
                Objects.equals(messages, chat.messages);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, type, members, messages);
    }
}
