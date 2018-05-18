package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "FRIEND", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(FriendEntityPK.class)
public class FriendEntity {
    private int user1Id;
    private int user2Id;
    private boolean confirmed;

    public FriendEntity(int user1Id, int user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public FriendEntity() {
    }

    @Id
    @Column(name = "USER_1_ID")
    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    @Id
    @Column(name = "USER_2_ID")
    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendEntity that = (FriendEntity) o;
        return user1Id == that.user1Id &&
                user2Id == that.user2Id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(user1Id, user2Id);
    }

    @Basic
    @Column(name = "CONFIRMED")
    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
