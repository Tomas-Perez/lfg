package api.rest.security.authentication.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class SignInJSON {
    private String email;
    private String password;

    @JsonCreator
    public SignInJSON(@JsonProperty(value = "email", required = true) String email,
                      @JsonProperty(value = "password", required = true) String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
