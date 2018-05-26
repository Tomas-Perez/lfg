package api.rest.common.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author Tomas Perez Molina
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
    private final ObjectMapper MAPPER;

    public ObjectMapperContextResolver() {
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();
//        MAPPER.registerModule(new JavaTimeModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return MAPPER;
    }
}
