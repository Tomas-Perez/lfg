package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_PLATFORM_FOR_POST", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(GamePlatformForPostPK.class)
public class GamePlatformForPost {
    private int postId;
    private int gamePlatformId;

    @Id
    @Column(name = "POST_ID")
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Id
    @Column(name = "GAME_PLATFORM_ID")
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
        GamePlatformForPost that = (GamePlatformForPost) o;
        return postId == that.postId &&
                gamePlatformId == that.gamePlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, gamePlatformId);
    }
}