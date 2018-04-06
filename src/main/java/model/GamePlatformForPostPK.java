package model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GamePlatformForPostPK implements Serializable {
    private int postId;
    private int gamePlatformId;

    @Column(name = "POST_ID")
    @Id
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Column(name = "GAME_PLATFORM_ID")
    @Id
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
        GamePlatformForPostPK that = (GamePlatformForPostPK) o;
        return postId == that.postId &&
                gamePlatformId == that.gamePlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, gamePlatformId);
    }
}
