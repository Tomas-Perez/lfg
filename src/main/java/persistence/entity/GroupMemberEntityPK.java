package persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GroupMemberEntityPK implements Serializable {
    private int groupId;
    private int memberId;

    public GroupMemberEntityPK(int groupId, int memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    public GroupMemberEntityPK() {}

    @Column(name = "GROUP_ID", nullable = false)
    @Id
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Column(name = "MEMBER_ID", nullable = false)
    @Id
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
        GroupMemberEntityPK that = (GroupMemberEntityPK) o;
        return groupId == that.groupId &&
                memberId == that.memberId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, memberId);
    }
}
