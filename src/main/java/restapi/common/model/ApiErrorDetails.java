package restapi.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Tomas Perez Molina
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDetails {

    private Integer status;
    private String title;
    private String message;
    private String path;

    public ApiErrorDetails(Integer status, String title, String message, String path) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.path = path;
    }

    public ApiErrorDetails() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static class Builder{
        private Integer status;
        private String title;
        private String message;
        private String path;

        public Builder withStatus(Integer status){
            this.status = status;
            return this;
        }

        public Builder withTitle(String title){
            this.title = title;
            return this;
        }

        public Builder withMessage(String message){
            this.message = message;
            return this;
        }

        public Builder withPath(String path){
            this.path = path;
            return this;
        }

        public ApiErrorDetails build(){
            return new ApiErrorDetails(status, title, message, path);
        }

    }
}