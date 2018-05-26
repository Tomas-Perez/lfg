package api.rest.signup.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class SignUpJSON {
    private String email;
    private String password;
    private String username;

    @JsonCreator
    public SignUpJSON(@JsonProperty(value = "email", required = true) String email,
                      @JsonProperty(value = "password", required = true) String password,
                      @JsonProperty(value = "username", required = true) String username) {
        this.email = email;
        this.password = password;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
