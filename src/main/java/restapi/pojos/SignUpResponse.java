package restapi.pojos;

/**
 * @author Tomas Perez Molina
 */
public class SignUpResponse {
    private Integer id;

    public SignUpResponse() {
    }

    public SignUpResponse(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
