package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
public class Group {
    private int groupId;
    private int slots;
    private Activity activityByActivityId;
    private ChatPlatform chatPlatformByChatPlatformId;

    @Id
    @Column(name = "GROUP_ID")
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "SLOTS")
    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupId == group.groupId &&
                slots == group.slots;
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupId, slots);
    }

    @ManyToOne
    @JoinColumn(name = "ACTIVITY_ID", referencedColumnName = "ACTIVITY_ID")
    public Activity getActivityByActivityId() {
        return activityByActivityId;
    }

    public void setActivityByActivityId(Activity activityByActivityId) {
        this.activityByActivityId = activityByActivityId;
    }

    @ManyToOne
    @JoinColumn(name = "CHAT_PLATFORM_ID", referencedColumnName = "CHAT_PLATFORM_ID")
    public ChatPlatform getChatPlatformByChatPlatformId() {
        return chatPlatformByChatPlatformId;
    }

    public void setChatPlatformByChatPlatformId(ChatPlatform chatPlatformByChatPlatformId) {
        this.chatPlatformByChatPlatformId = chatPlatformByChatPlatformId;
    }
}
