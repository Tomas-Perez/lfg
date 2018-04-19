package manager;


import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

/**
 * @author Tomas Perez Molina
 */
public class ConstraintException extends RuntimeException{

    private final static String FORMAT = "integrity constraint violation: unique constraint or index violation: ";

    private String constraintName;

    public ConstraintException(PersistenceException exc) {
        super(exc.getCause().getCause().getMessage());
        try {
            constraintName = this.getMessage().substring(FORMAT.length());
        } catch (Exception e){
            constraintName = null;
        }
    }

    public String getConstraintName() {
        return constraintName;
    }
}
