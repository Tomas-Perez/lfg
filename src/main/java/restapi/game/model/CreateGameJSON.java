package restapi.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class CreateGameJSON {
    private String name;

    @JsonCreator
    public CreateGameJSON(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
