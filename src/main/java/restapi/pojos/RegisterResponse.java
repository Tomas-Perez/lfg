package restapi.pojos;

/**
 * @author Tomas Perez Molina
 */
public class RegisterResponse {
    private Integer id;

    public RegisterResponse() {
    }

    public RegisterResponse(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
