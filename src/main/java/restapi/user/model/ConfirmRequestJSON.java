package restapi.user.model;

/**
 * @author Tomas Perez Molina
 */
public class ConfirmRequestJSON {
    private int id;

    public ConfirmRequestJSON(int id) {
        this.id = id;
    }

    public ConfirmRequestJSON(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
