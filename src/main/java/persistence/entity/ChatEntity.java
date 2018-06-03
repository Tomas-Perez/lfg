package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT", schema = "PUBLIC", catalog = "PUBLIC")
public class ChatEntity {
    private int id;
    private ChatType type;

    public ChatEntity() {
        type = ChatType.GROUP;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Enumerated
    @Column(name = "TYPE", nullable = false, columnDefinition = "smallint")
    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public enum ChatType {
        PRIVATE, GROUP
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatEntity)) return false;
        ChatEntity that = (ChatEntity) o;
        return id == that.id &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, type);
    }
}
