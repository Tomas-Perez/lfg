package restapi.common.filter;

import javax.enterprise.context.Dependent;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * No one likes OPTIONS
 *
 * @author Tomas Perez Molina
 */
@Provider
@PreMatching
@Dependent
public class CORSRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext){
        if ("OPTIONS".equals(requestContext.getRequest().getMethod())) {
            Response r = Response.ok().build();
            requestContext.abortWith(r);
        }
    }
}
