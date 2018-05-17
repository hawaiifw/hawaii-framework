package org.hawaiiframework.logging.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Resolver for UUID values from HTTP Servlet Request Headers.
 */
public class UuidResolver {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UuidResolver.class);

    /**
     * Resolve the UUID from the header with name {@code headerName}.
     * <p>
     * If the value is not set in the {@code request} then a new UUID will be generated.
     */
    public UUID resolve(final HttpServletRequest request, final String headerName) {
        UUID uuid = null;

        final String txIdHeader = request.getHeader(headerName);
        if (StringUtils.isNotBlank(txIdHeader)) {
            LOGGER.trace("Found header '{}' with value '{}' in request.", headerName, txIdHeader);
            try {
                uuid = UUID.fromString(txIdHeader);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Could not create UUID from header.", e);
            }
        }

        if (uuid == null) {
            uuid = UUID.randomUUID();
            LOGGER.trace("Generated new UUID '{}'.", uuid);
        }

        return uuid;
    }
}
