package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_IN_PLATFORM", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(GameInPlatformEntityPK.class)
public class GameInPlatformEntity {
    private int gamePlatformId;
    private int gameId;

    @Id
    @Column(name = "GAME_PLATFORM_ID", nullable = false)
    public int getGamePlatformId() {
        return gamePlatformId;
    }

    public void setGamePlatformId(int gamePlatformId) {
        this.gamePlatformId = gamePlatformId;
    }

    @Id
    @Column(name = "GAME_ID", nullable = false)
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
        GameInPlatformEntity that = (GameInPlatformEntity) o;
        return gamePlatformId == that.gamePlatformId &&
                gameId == that.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gamePlatformId, gameId);
    }
}
