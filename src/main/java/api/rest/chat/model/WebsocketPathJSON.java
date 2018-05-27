package api.rest.chat.model;

/**
 * @author Tomas Perez Molina
 */
public class WebsocketPathJSON {
    private String socketPath;

    public WebsocketPathJSON(String socketPath) {
        this.socketPath = socketPath;
    }

    public WebsocketPathJSON() {}

    public String getSocketPath() {
        return socketPath;
    }

    public void setSocketPath(String socketPath) {
        this.socketPath = socketPath;
    }

    @Override
    public String toString() {
        return "WebsocketPathJSON{" +
                "socketPath='" + socketPath + '\'' +
                '}';
    }
}
