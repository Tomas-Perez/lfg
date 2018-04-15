package restapi.filters;

import managers.FactoryProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */
@Provider
public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext){
        System.out.println("UNFILTERING");
        FactoryProvider.getFactory().getCurrentSession().close();
    }
}
