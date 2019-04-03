package org.hawaiiframework.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utility class to get host related information.
 *
 * @since 3.0.0.M6
 */
public class HostResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostResolver.class);

    /**
     * Returns the host's base url consisting of the scheme and host.
     *
     * @return the base url
     */
    public String getBaseUrl() {
        final var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final var request = requestAttributes.getRequest();
        final var scheme = request.getScheme();
        final var host = request.getHeader(HttpHeaders.HOST);
        LOGGER.debug("Resolved scheme '{}' and host '{}'", scheme, host);
        return request.getScheme() + "://" + host;
    }
}
