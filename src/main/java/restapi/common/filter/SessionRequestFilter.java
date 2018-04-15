package restapi.common.filter;

import manager.FactoryProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */

@Provider
public class SessionRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx){
        System.out.println("FILTERING");
        FactoryProvider.getFactory().openSession();
    }
}
