package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "GAME_IN_PLATFORM", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(GameInPlatformPK.class)
public class GameInPlatform {
    private int gameId;
    private int gamePlatformId;
    private Game gameByGameId;
    private GamePlatform gamePlatformByGamePlatformId;

    @Id
    @Column(name = "GAME_ID")
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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
        GameInPlatform that = (GameInPlatform) o;
        return gameId == that.gameId &&
                gamePlatformId == that.gamePlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gameId, gamePlatformId);
    }

    @ManyToOne
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", nullable = false)
    public Game getGameByGameId() {
        return gameByGameId;
    }

    public void setGameByGameId(Game gameByGameId) {
        this.gameByGameId = gameByGameId;
    }

    @ManyToOne
    @JoinColumn(name = "GAME_PLATFORM_ID", referencedColumnName = "GAME_PLATFORM_ID", nullable = false)
    public GamePlatform getGamePlatformByGamePlatformId() {
        return gamePlatformByGamePlatformId;
    }

    public void setGamePlatformByGamePlatformId(GamePlatform gamePlatformByGamePlatformId) {
        this.gamePlatformByGamePlatformId = gamePlatformByGamePlatformId;
    }
}
