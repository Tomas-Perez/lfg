package api.rest.common.exception;

/**
 * @author Tomas Perez Molina
 */
public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }
}
