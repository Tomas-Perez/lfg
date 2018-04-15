package restapi.security.authorization.exception.mapper;


import restapi.security.authorization.exception.AccessDeniedException;
import restapi.common.model.ApiErrorDetails;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for {@link AccessDeniedException}s.
 */
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(AccessDeniedException exception) {

        Status status = Status.FORBIDDEN;

        ApiErrorDetails errorDetails = new ApiErrorDetails.Builder()
                .withStatus(status.getStatusCode())
                .withTitle(status.getReasonPhrase())
                .withMessage("You don't have enough permissions to perform this action.")
                .withPath(uriInfo.getAbsolutePath().getPath())
                .build();

        return Response
                .status(status)
                .entity(errorDetails)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}