package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_PLATFORM_FOR_POST", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(GamePlatformForPostEntityPK.class)
public class GamePlatformForPostEntity {
    private int postId;
    private int gamePlatformId;

    public GamePlatformForPostEntity(int postId, int gamePlatformId) {
        this.postId = postId;
        this.gamePlatformId = gamePlatformId;
    }

    public GamePlatformForPostEntity() {
    }

    @Id
    @Column(name = "POST_ID", nullable = false)
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Id
    @Column(name = "GAME_PLATFORM_ID", nullable = false)
    public int getGamePlatformId() {
        return gamePlatformId;
    }

    public void setGamePlatformId(int gamePlatformId) {
        this.gamePlatformId = gamePlatformId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlatformForPostEntity that = (GamePlatformForPostEntity) o;
        return postId == that.postId &&
                gamePlatformId == that.gamePlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, gamePlatformId);
    }
}
