package model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class GameInPlatformPK implements Serializable {
    private int gameId;
    private int gamePlatformId;

    @Column(name = "GAME_ID")
    @Id
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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
        GameInPlatformPK that = (GameInPlatformPK) o;
        return gameId == that.gameId &&
                gamePlatformId == that.gamePlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gameId, gamePlatformId);
    }
}
