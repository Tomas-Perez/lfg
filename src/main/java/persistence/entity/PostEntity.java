package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "POST", schema = "PUBLIC", catalog = "PUBLIC")
public class PostEntity {
    private int id;
    private LocalDateTime date;
    private String description;
    private Integer activityId;
    private Integer groupId;
    private int ownerId;

    public PostEntity(int id, String description, LocalDateTime date, Integer activityID, int ownerID, Integer groupID) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.activityId = activityID;
        this.ownerId = ownerID;
        this.groupId = groupID;
    }

    public PostEntity() {
    }

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "DATE", nullable = false)
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Basic
    @Column(name = "DESCRIPTION", length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "ACTIVITY_ID")
    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    @Basic
    @Column(name = "GROUP_ID")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "OWNER_ID", nullable = false)
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostEntity that = (PostEntity) o;
        return id == that.id &&
                ownerId == that.ownerId &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                Objects.equals(activityId, that.activityId) &&
                Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, date, description, activityId, groupId, ownerId);
    }

    @Override
    public String toString() {
        return "PostEntity{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", activityId=" + activityId +
                ", groupId=" + groupId +
                ", ownerId=" + ownerId +
                '}';
    }
}
