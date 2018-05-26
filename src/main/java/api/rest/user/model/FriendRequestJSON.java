package api.rest.user.model;

/**
 * @author Tomas Perez Molina
 */
public class FriendRequestJSON {
    private int id;

    public FriendRequestJSON(int id) {
        this.id = id;
    }

    public FriendRequestJSON(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
