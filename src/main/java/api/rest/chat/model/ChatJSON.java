package api.rest.chat.model;

import api.rest.group.model.MemberJSON;
import persistence.entity.ChatEntity;
import persistence.model.Chat;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tomas Perez Molina
 */
public class ChatJSON {
    private int id;
    private ChatType type;
    private List<MemberJSON> members;
    private List<MessageJSON> messages;

    public ChatJSON(int id, ChatType type, List<MemberJSON> members, List<MessageJSON> messages) {
        this.id = id;
        this.type = type;
        this.members = members;
        this.messages = messages;
    }

    public ChatJSON(Chat chat) {
        this.id = chat.getId();
        final ChatEntity.ChatType type = chat.getType();
        this.type = type == ChatEntity.ChatType.PRIVATE? ChatType.PRIVATE : ChatType.GROUP;
        this.members = chat.getMembers().stream().map(MemberJSON::new).collect(Collectors.toList());
        this.messages = chat.getMessages().stream().map(MessageJSON::new).collect(Collectors.toList());
    }

    public ChatJSON() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MemberJSON> getMembers() {
        return members;
    }

    public void setMembers(List<MemberJSON> members) {
        this.members = members;
    }

    public List<MessageJSON> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageJSON> messages) {
        this.messages = messages;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatJSON)) return false;
        ChatJSON chatJSON = (ChatJSON) o;
        return id == chatJSON.id &&
                type == chatJSON.type &&
                Objects.equals(new HashSet<>(members), new HashSet<>(chatJSON.members)) &&
                Objects.equals(new HashSet<>(messages), new HashSet<>(chatJSON.messages));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, members, messages);
    }

    @Override
    public String toString() {
        return "ChatJSON{" +
                "id=" + id +
                ", type=" + type +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
