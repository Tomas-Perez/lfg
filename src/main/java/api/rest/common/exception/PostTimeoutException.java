package api.rest.common.exception;

/**
 * @author Tomas Perez Molina
 */
public class PostTimeoutException extends RuntimeException {

    private long timeout;

    public PostTimeoutException(long timeout) {
        super(String.format("Too many posts, retry after %d", timeout));
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }
}
