package api.websocket.post.filter;

import api.common.postfilter.FilterParams;
import common.postfilter.FilterData;
import api.common.postfilter.FilterParameterDecoder;
import api.websocket.post.filter.exception.MalformedParameterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds a FilteredPrincipal to the request with the filters passed as Query Parameters.
 *
 * @author Tomas Perez Molina
 */

@WebFilter("/websockets/posts/*")
public class PostFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(PostFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        String[] filters = request.getParameterValues("filter");
        String[] empty = {};
        List<String> filterParamList = Arrays.asList(filters == null? empty : filters);
        FilterParams filterParams = new FilterParams(filterParamList);

        filterChain.doFilter(new FilteredRequest(request, filterParams.getFilterData()), response);
    }

    @Override
    public void destroy() {}

    /**
     * Wrapper for a {@link HttpServletRequest} which decorates a {@link HttpServletRequest} by adding a {@link Principal} to it.
     *
     * @author Tomas Perez Molina
     */

    private static class FilteredRequest extends HttpServletRequestWrapper {

        private List<FilterData> filters;
        private Principal principal;

        private FilteredRequest(HttpServletRequest request, List<FilterData> filters) {
            super(request);
            this.principal = request.getUserPrincipal();
            this.filters = filters;
        }

        @Override
        public Principal getUserPrincipal() {
            return new FilteredPrincipal(principal, filters);
        }
    }
}