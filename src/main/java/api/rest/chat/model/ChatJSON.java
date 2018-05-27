package api.rest.chat.model;

import api.rest.group.model.MemberJSON;
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
    private List<MemberJSON> members;
    private List<MessageJSON> messages;

    public ChatJSON(int id, List<MemberJSON> members, List<MessageJSON> messages) {
        this.id = id;
        this.members = members;
        this.messages = messages;
    }

    public ChatJSON(Chat chat) {
        this.id = chat.getId();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatJSON)) return false;
        ChatJSON chatJSON = (ChatJSON) o;
        return id == chatJSON.id &&
                Objects.equals(new HashSet<>(members), new HashSet<>(chatJSON.members)) &&
                Objects.equals(new HashSet<>(messages), new HashSet<>(chatJSON.messages));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, members, messages);
    }

    @Override
    public String toString() {
        return "ChatJSON{" +
                "id=" + id +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
