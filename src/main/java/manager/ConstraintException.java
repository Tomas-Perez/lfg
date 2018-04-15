package manager;

import org.hibernate.exception.ConstraintViolationException;

/**
 * @author Tomas Perez Molina
 */
public class ConstraintException extends RuntimeException{

    private final static String FORMAT = "integrity constraint violation: unique constraint or index violation: ";

    private String constraintName;

    public ConstraintException(ConstraintViolationException exc) {
        super(exc.getCause().getMessage());
        constraintName = this.getMessage().substring(FORMAT.length());
    }

    public String getConstraintName() {
        return constraintName;
    }
}
