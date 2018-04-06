package model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class OwnsGamePK implements Serializable {
    private int ownerId;
    private int gameId;

    @Column(name = "OWNER_ID")
    @Id
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "GAME_ID")
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
        OwnsGamePK that = (OwnsGamePK) o;
        return ownerId == that.ownerId &&
                gameId == that.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(ownerId, gameId);
    }
}
