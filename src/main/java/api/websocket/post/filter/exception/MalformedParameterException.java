package api.websocket.post.filter.exception;

/**
 * @author Tomas Perez Molina
 */
public class MalformedParameterException extends RuntimeException {
    public MalformedParameterException(String message) {
        super(message);
    }
}
