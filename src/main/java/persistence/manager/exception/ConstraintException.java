package persistence.manager.exception;

/**
 * @author Tomas Perez Molina
 */
public class ConstraintException extends RuntimeException{

    private final static String MSG = " already exists";

    private String constraintName;

    public ConstraintException(String constraintName) {
        super(constraintName + MSG);
        this.constraintName = constraintName;
    }

    public String getConstraintName() {
        return constraintName;
    }
}
