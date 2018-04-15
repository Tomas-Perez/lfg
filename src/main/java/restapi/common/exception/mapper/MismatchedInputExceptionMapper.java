package restapi.common.exception.mapper;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import restapi.common.model.ApiErrorDetails;

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
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(MismatchedInputException exception) {
        Response.Status status = Response.Status.BAD_REQUEST;

        ApiErrorDetails errorDetails = new ApiErrorDetails.Builder()
                .withStatus(status.getStatusCode())
                .withTitle(status.getReasonPhrase())
                .withMessage(exception.getMessage().split("\n")[0])
                .withPath(uriInfo.getAbsolutePath().getPath())
                .build();

        return Response
                .status(status)
                .entity(errorDetails)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
