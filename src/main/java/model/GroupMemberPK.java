package model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GroupMemberPK implements Serializable {
    private int groupId;
    private int userId;

    @Column(name = "GROUP_ID")
    @Id
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Column(name = "USER_ID")
    @Id
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberPK that = (GroupMemberPK) o;
        return groupId == that.groupId &&
                userId == that.userId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, userId);
    }
}
