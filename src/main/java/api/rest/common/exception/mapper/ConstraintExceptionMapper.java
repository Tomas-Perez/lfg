package api.rest.common.exception.mapper;

import persistence.manager.exception.ConstraintException;
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
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintException exception) {
        Response.Status status = Response.Status.CONFLICT;

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
