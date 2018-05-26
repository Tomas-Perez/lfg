package api.rest.security.common.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public enum Authority {
    USER, ADMIN;

    public static Set<Authority> getAuthoritySet(boolean isAdmin){
        Set<Authority> s = new HashSet<>();
        s.add(USER);
        if(isAdmin) s.add(ADMIN);
        return s;
    }
}
