package api.websocket.post.filter;

import common.postfilter.FilterPair;

import java.security.Principal;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public class FilteredPrincipal implements Principal {
    private Principal principal;
    private List<FilterPair> filters;

    public FilteredPrincipal(Principal principal, List<FilterPair> filters) {
        this.filters = filters;
        this.principal = principal;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public List<FilterPair> getFilters() {
        return filters;
    }
}
