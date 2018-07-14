package api.rest.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public enum ChatType {
    @JsonProperty("group") GROUP,
    @JsonProperty("private") PRIVATE
}
