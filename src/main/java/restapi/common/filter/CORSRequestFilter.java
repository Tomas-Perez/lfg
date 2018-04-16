package restapi.common.filter;

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
public class CORSRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext){
        if ("OPTIONS".equals(requestContext.getRequest().getMethod())) {
            Response r = Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .build();
            requestContext.abortWith(r);
        }
    }
}
