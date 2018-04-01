package managers;

import org.hibernate.exception.ConstraintViolationException;

/**
 * @author Tomas Perez Molina
 */
public class ConstraintException extends RuntimeException{

    public ConstraintException(ConstraintViolationException exc) {
        super(exc.getCause().getMessage());
    }
}
