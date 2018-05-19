package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CREW", schema = "PUBLIC", catalog = "PUBLIC")
public class GroupEntity {
    private int id;
    private int slots;
    private int activityId;
    private Integer chatPlatformId;
    private Integer gamePlatformId;
    private int ownerId;

    public GroupEntity(int slots, int activityId, Integer chatPlatformId, Integer gamePlatformId, int ownerId) {
        this.slots = slots;
        this.activityId = activityId;
        this.chatPlatformId = chatPlatformId;
        this.gamePlatformId = gamePlatformId;
        this.ownerId = ownerId;
    }

    public GroupEntity() {
    }

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SLOTS")
    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    @Basic
    @Column(name = "ACTIVITY_ID")
    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    @Basic
    @Column(name = "CHAT_PLATFORM_ID")
    public Integer getChatPlatformId() {
        return chatPlatformId;
    }

    public void setChatPlatformId(Integer chatPlatformId) {
        this.chatPlatformId = chatPlatformId;
    }

    @Basic
    @Column(name = "GAME_PLATFORM_ID")
    public Integer getGamePlatformId() {
        return gamePlatformId;
    }

    public void setGamePlatformId(Integer gamePlatformId) {
        this.gamePlatformId = gamePlatformId;
    }

    @Transient
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
        GroupEntity that = (GroupEntity) o;
        return id == that.id &&
                Objects.equals(slots, that.slots) &&
                Objects.equals(activityId, that.activityId) &&
                Objects.equals(chatPlatformId, that.chatPlatformId) &&
                Objects.equals(gamePlatformId, that.gamePlatformId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, slots, activityId, chatPlatformId, gamePlatformId);
    }
}
