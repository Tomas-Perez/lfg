package api.websocket.common.config;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.server.ServerEndpointConfig;

/**
 * CDI Configurator for websocket endpoints. CDI does not work on websockets without this.
 *
 * @author Tomas Perez Molina
 */
public class CdiAwareConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return CDI.current().select(endpointClass).get();
    }
}
