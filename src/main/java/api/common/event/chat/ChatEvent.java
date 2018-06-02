package api.common.event.chat;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class ChatEvent {
    private int id;
    private Set<Integer> notifySet;

    public ChatEvent(int id, Set<Integer> notifyList) {
        this.id = id;
        this.notifySet = notifyList;
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getNotifySet() {
        return notifySet;
    }
}
