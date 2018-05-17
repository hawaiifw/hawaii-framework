package org.hawaiiframework.security.sso.web.filter;

import org.hawaiiframework.security.sso.model.HawaiiSsoAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * A Servlet filter to use in the Spring Security Filter chain.
 * <p>
 * This filter validates OAUTH2 tokens. Difference with the default OAuth2 filter is:
 * <li>tokens are cached</li>
 * <li>if the token is not found in the cache, the token is validated against the SSO</li>
 * <p>
 * {@inheritDoc}
 */
public class HawaiiSsoAuthenticationFilter implements Filter {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HawaiiSsoAuthenticationFilter.class);

    /**
     * The authorization header .
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * The start of the (accepted) authorization header.
     */
    private static final String BEARER = "Bearer ";

    /**
     * The authentication manager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * The constructor.
     */
    public HawaiiSsoAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = requireNonNull(authenticationManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing to register
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
                LOGGER.trace("Authenticating with Hawaii SSO access token.");
                final HawaiiSsoAuthenticationToken token = new HawaiiSsoAuthenticationToken(authorizationHeader.substring(BEARER.length()));

                final Authentication authenticationResult = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            }
        } catch (AuthenticationException failed) {
            LOGGER.debug("Failed to authenticate the user, {}.", failed.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Nothing to cleanup.
    }
}
