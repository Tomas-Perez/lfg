package restapi.authentication.exception.mapper;

import restapi.authentication.exception.AuthenticationTokenRefreshmentException;
import restapi.common.model.ApiErrorDetails;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link AuthenticationTokenRefreshmentException}s.
 *
 * @author cassiomolin
 */
@Provider
public class AuthenticationTokenRefreshmentExceptionMapper implements ExceptionMapper<AuthenticationTokenRefreshmentException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AuthenticationTokenRefreshmentException exception) {

        Status status = Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails.Builder()
                .withStatus(status.getStatusCode())
                .withTitle(status.getReasonPhrase())
                .withMessage("The authentication token cannot be refreshed.")
                .withPath(uriInfo.getAbsolutePath().getPath())
                .build();

        return Response
                .status(status)
                .entity(errorDetails)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}