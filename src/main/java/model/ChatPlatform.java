package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT_PLATFORM", schema = "PUBLIC", catalog = "PUBLIC")
public class ChatPlatform {
    private int chatPlatformId;
    private String name;
    private String image;

    @Id
    @Column(name = "CHAT_PLATFORM_ID")
    public int getChatPlatformId() {
        return chatPlatformId;
    }

    public void setChatPlatformId(int chatPlatformId) {
        this.chatPlatformId = chatPlatformId;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "IMAGE")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPlatform that = (ChatPlatform) o;
        return chatPlatformId == that.chatPlatformId &&
                Objects.equals(name, that.name) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(chatPlatformId, name, image);
    }
}
