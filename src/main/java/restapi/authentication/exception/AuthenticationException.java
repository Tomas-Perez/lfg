package restapi.authentication.exception;

/**
 * @author Tomas Perez Molina
 */
public class AuthenticationException extends RuntimeException{

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
