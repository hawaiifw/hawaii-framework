package org.hawaiiframework.logging.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to determine the client IP address.
 */
public class HostResolver {

    @SuppressWarnings("PMD.CommentRequired")
    private static final String FRONTEND_IP_HEADER = "X-Hawaii-Frontend-IP-Address";

    /**
     * Extract the client IP address from the HttpServletRequest.
     *
     * @param request the request
     * @return the IP address
     */
    public String getFrontendIPAddress(final HttpServletRequest request) {
        String address = request.getHeader(FRONTEND_IP_HEADER);
        if (StringUtils.isBlank(address)) {
            address = request.getRemoteAddr();
        }
        return address;
    }

}
