package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GROUP_MEMBER", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(GroupMemberEntityPK.class)
public class GroupMemberEntity {
    private int groupId;
    private int memberId;
    private boolean owner;

    public GroupMemberEntity(int groupId, int memberId, boolean owner) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.owner = owner;
    }

    public GroupMemberEntity() {
    }

    @Basic
    @Column(name = "OWNER", nullable = false)
    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    @Id
    @Column(name = "GROUP_ID", nullable = false)
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Id
    @Column(name = "MEMBER_ID", nullable = false)
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberEntity that = (GroupMemberEntity) o;
        return groupId == that.groupId &&
                memberId == that.memberId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, memberId);
    }
}
