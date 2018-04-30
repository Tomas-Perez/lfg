package restapi.activity.model;

/**
 * @author Tomas Perez Molina
 */
public class UpdateActivityJSON {
    private String name;
    private int gameID;

    public UpdateActivityJSON(String name, int gameID) {
        this.name = name;
        this.gameID = gameID;
    }

    public UpdateActivityJSON() {
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
