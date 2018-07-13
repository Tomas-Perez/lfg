package api.common.event.group;

import api.rest.user.model.BasicUserData;

/**
 * @author Tomas Perez Molina
 */
public class MemberEvent {
    private int groupID;
    private BasicUserData member;

    public MemberEvent(int groupID, BasicUserData member) {
        this.groupID = groupID;
        this.member = member;
    }

    public int getGroupID() {
        return groupID;
    }

    public BasicUserData getMember() {
        return member;
    }
}
