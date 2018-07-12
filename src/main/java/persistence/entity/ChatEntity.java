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
    private Integer groupId;

    public ChatEntity(int groupId) {
        this.groupId = groupId;
    }

    public ChatEntity(){
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

    @Transient
    public ChatType getType() {
        return groupId == null? ChatType.PRIVATE : ChatType.GROUP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatEntity)) return false;
        ChatEntity that = (ChatEntity) o;
        return id == that.id &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupId);
    }

    @Basic
    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public enum ChatType {
        PRIVATE, GROUP
    }
}
