package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "OWNS_GAME", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(OwnsGamePK.class)
public class OwnsGame {
    private int ownerId;
    private int gameId;
    private Game gameByGameId;

    @Id
    @Column(name = "OWNER_ID")
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Id
    @Column(name = "GAME_ID")
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
        OwnsGame ownsGame = (OwnsGame) o;
        return ownerId == ownsGame.ownerId &&
                gameId == ownsGame.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(ownerId, gameId);
    }

    @ManyToOne
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", nullable = false)
    public Game getGameByGameId() {
        return gameByGameId;
    }

    public void setGameByGameId(Game gameByGameId) {
        this.gameByGameId = gameByGameId;
    }
}
