package persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GameInPlatformEntityPK implements Serializable {
    private int gamePlatformId;
    private int gameId;

    @Column(name = "GAME_PLATFORM_ID", nullable = false)
    @Id
    public int getGamePlatformId() {
        return gamePlatformId;
    }

    public void setGamePlatformId(int gamePlatformId) {
        this.gamePlatformId = gamePlatformId;
    }

    @Column(name = "GAME_ID", nullable = false)
    @Id
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameInPlatformEntityPK that = (GameInPlatformEntityPK) o;
        return gamePlatformId == that.gamePlatformId &&
                gameId == that.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gamePlatformId, gameId);
    }
}
