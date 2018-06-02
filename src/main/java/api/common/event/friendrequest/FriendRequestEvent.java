package api.common.event.friendrequest;

import api.rest.user.model.BasicUserData;

/**
 * @author Tomas Perez Molina
 */
public class FriendRequestEvent {
    private BasicUserData sender;
    private BasicUserData receiver;

    public FriendRequestEvent(BasicUserData sender, BasicUserData receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public BasicUserData getSender() {
        return sender;
    }

    public BasicUserData getReceiver() {
        return receiver;
    }
}
