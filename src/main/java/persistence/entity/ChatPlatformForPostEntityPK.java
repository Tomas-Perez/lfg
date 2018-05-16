package persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ChatPlatformForPostEntityPK implements Serializable {
    private int postId;
    private int chatPlatformId;

    @Column(name = "POST_ID", nullable = false)
    @Id
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Column(name = "CHAT_PLATFORM_ID", nullable = false)
    @Id
    public int getChatPlatformId() {
        return chatPlatformId;
    }

    public void setChatPlatformId(int chatPlatformId) {
        this.chatPlatformId = chatPlatformId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPlatformForPostEntityPK that = (ChatPlatformForPostEntityPK) o;
        return postId == that.postId &&
                chatPlatformId == that.chatPlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, chatPlatformId);
    }
}
