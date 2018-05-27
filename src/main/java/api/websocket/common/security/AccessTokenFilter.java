package api.websocket.common.security;

import api.rest.security.authentication.service.AuthenticationTokenDetails;
import api.rest.security.authentication.service.AuthenticationTokenService;
import persistence.entity.UserEntity;
import persistence.manager.UserManager;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

/**
 * Access token filter for the websockets. Requests without a valid access token are refused with a <code>403</code>.
 *
 * @author Tomas Perez Molina
 */

@WebFilter("/websockets/*")
public class AccessTokenFilter implements Filter {

    @Inject
    private AuthenticationTokenService authenticationTokenService;

    @Inject
    private UserManager userManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        String token = request.getParameter("access-token");
        if (token == null || token.trim().isEmpty()) {
            returnForbiddenError(response, "Authentication is required to connect.");
            return;
        }

        AuthenticationTokenDetails tokenDetails = authenticationTokenService.parseToken(token);

        Optional<UserEntity> optionalUser = userManager.getByEmail(tokenDetails.getEmail());
        if(optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            filterChain.doFilter(new AuthenticatedRequest(request, Integer.toString(user.getId())), servletResponse);
        } else {
            returnForbiddenError(response, "Invalid access token");
        }
    }

    private void returnForbiddenError(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
    }

    @Override
    public void destroy() {}

    /**
     * Wrapper for a {@link HttpServletRequest} which decorates a {@link HttpServletRequest} by adding a {@link Principal} to it.
     *
     * @author Tomas Perez Molina
     */

    private static class AuthenticatedRequest extends HttpServletRequestWrapper {

        private String id;

        private AuthenticatedRequest(HttpServletRequest request, String id) {
            super(request);
            this.id = id;
        }

        @Override
        public Principal getUserPrincipal() {
            return () -> id;
        }
    }
}