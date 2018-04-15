package restapi.authentication.exception.mapper;

import restapi.authentication.exception.InvalidAuthenticationTokenException;
import restapi.common.model.ApiErrorDetails;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link InvalidAuthenticationTokenException}s.
 *
 * @author cassiomolin
 */
@Provider
public class InvalidAuthenticationTokenExceptionMapper implements ExceptionMapper<InvalidAuthenticationTokenException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidAuthenticationTokenException exception) {

        Status status = Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails.Builder()
                .withStatus(status.getStatusCode())
                .withTitle(status.getReasonPhrase())
                .withMessage(exception.getMessage())
                .withPath(uriInfo.getAbsolutePath().getPath())
                .build();

        return Response
                .status(status)
                .entity(errorDetails)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}