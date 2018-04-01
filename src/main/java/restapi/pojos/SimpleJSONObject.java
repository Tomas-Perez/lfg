package restapi.pojos;

/**
 * @author Tomas Perez Molina
 */
public class SimpleJSONObject {

    private Status status;
    private Object data;
    private Error error;

    public SimpleJSONObject(Status status, Object data, Error error){
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public SimpleJSONObject(Error error) {
        this.status = Status.ERROR;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public enum Status {
        SUCCESS("success"), FAIL("fail"), ERROR("error");

        private final String s;

        Status(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    public static class Error {
        int code;
        String message;

        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
