package api.websocket.common.security;

import api.rest.user.model.BasicUserData;

import java.security.Principal;

/**
 * @author Tomas Perez Molina
 */
public class AuthenticatedPrincipal implements Principal {
    private BasicUserData data;

    public AuthenticatedPrincipal(BasicUserData data) {
        this.data = data;
    }

    @Override
    public String getName() {
        return data.getUsername();
    }

    public int getId() {
        return data.getId();
    }

    public BasicUserData getData() {
        return data;
    }
}
