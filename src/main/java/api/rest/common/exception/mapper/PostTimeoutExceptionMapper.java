package api.rest.common.exception.mapper;

import api.rest.common.exception.BadRequestException;
import api.rest.common.exception.PostTimeoutException;
import api.rest.common.model.ApiErrorDetails;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */

@Provider
public class PostTimeoutExceptionMapper implements ExceptionMapper<PostTimeoutException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(PostTimeoutException exception) {
        int statusCode = 429;
        String reasonPhrase = "Too many requests";

        ApiErrorDetails errorDetails = new ApiErrorDetails.Builder()
                .withStatus(statusCode)
                .withTitle(reasonPhrase)
                .withMessage(exception.getMessage())
                .withPath(uriInfo.getAbsolutePath().getPath())
                .build();

        return Response
                .status(statusCode)
                .entity(errorDetails)
                .header("Retry-After", exception.getTimeout())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
