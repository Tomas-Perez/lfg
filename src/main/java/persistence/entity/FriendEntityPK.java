package persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class FriendEntityPK implements Serializable {
    private int user1Id;
    private int user2Id;

    public FriendEntityPK(int user1Id, int user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public FriendEntityPK() {
    }

    @Column(name = "USER_1_ID")
    @Id
    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    @Column(name = "USER_2_ID")
    @Id
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
        FriendEntityPK that = (FriendEntityPK) o;
        return user1Id == that.user1Id &&
                user2Id == that.user2Id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(user1Id, user2Id);
    }
}
