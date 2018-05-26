package api.rest.common.exception.mapper;

import api.rest.common.exception.BadRequestException;
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
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(BadRequestException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;
        System.out.println("MAPPING something");

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
