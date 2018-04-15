package restapi.security.authentication.model;

/**
 * @author Tomas Perez Molina
 */
public class AuthenticationToken {
    private String token;

    public AuthenticationToken(String token) {
        this.token = token;
    }

    public AuthenticationToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
