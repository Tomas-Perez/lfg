package api.rest.activity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class CreateActivityJSON {
    private String name;
    private int gameID;

    public CreateActivityJSON(@JsonProperty(value = "name", required = true) String name,
                              @JsonProperty(value = "gameID", required = true) int gameID) {
        this.name = name;
        this.gameID = gameID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
