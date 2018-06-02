package api.common.event.friendrequest;

import api.rest.user.model.BasicUserData;

/**
 * @author Tomas Perez Molina
 */
public class FriendEvent {
    private BasicUserData user1;
    private BasicUserData user2;

    public FriendEvent(BasicUserData user1, BasicUserData user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public BasicUserData getUser1() {
        return user1;
    }

    public BasicUserData getUser2() {
        return user2;
    }
}
