package restapi.authentication.exception;


/**
 * Thrown if an authentication token is invalid.
 *
 */
public class InvalidAuthenticationTokenException extends RuntimeException {

    public InvalidAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
