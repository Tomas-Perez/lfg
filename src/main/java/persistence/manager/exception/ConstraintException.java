package persistence.manager.exception;

/**
 * @author Tomas Perez Molina
 */
public class ConstraintException extends RuntimeException{

    private final static String MSG = "ContraintException: ";

    private String constraintName;

    public ConstraintException(String constraintName) {
        super(MSG + constraintName);
        this.constraintName = constraintName;
    }

    public String getConstraintName() {
        return constraintName;
    }
}
