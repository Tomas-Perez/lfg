package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "OWNS_GAME", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(OwnsGameEntityPK.class)
public class OwnsGameEntity {
    private int ownerId;
    private int gameId;

    @Id
    @Column(name = "OWNER_ID", nullable = false)
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
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
        OwnsGameEntity that = (OwnsGameEntity) o;
        return ownerId == that.ownerId &&
                gameId == that.gameId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(ownerId, gameId);
    }

    @Override
    public String toString() {
        return "OwnsGameEntity{" +
                "ownerId=" + ownerId +
                ", gameId=" + gameId +
                '}';
    }
}
