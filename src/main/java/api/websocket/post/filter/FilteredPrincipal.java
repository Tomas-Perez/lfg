package api.websocket.post.filter;

import common.postfilter.FilterData;

import java.security.Principal;
import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public class FilteredPrincipal implements Principal {
    private Principal principal;
    private List<FilterData> filters;

    public FilteredPrincipal(Principal principal, List<FilterData> filters) {
        this.filters = filters;
        this.principal = principal;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public List<FilterData> getFilters() {
        return filters;
    }
}
