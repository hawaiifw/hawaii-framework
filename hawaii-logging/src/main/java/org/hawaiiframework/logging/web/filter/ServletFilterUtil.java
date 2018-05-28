package org.hawaiiframework.logging.web.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility for Servlet Filters.
 */
public final class ServletFilterUtil {

    /**
     * The request attribute to set when a response is a redirect.
     */
    private static final String REDIRECT_ATTRIBUTE = "is_internal_redirect";

    /**
     * Constructor.
     */
    private ServletFilterUtil() {
        // Utility class constructor.
    }

    /**
     * Returns {@code true} if the response for this request is a request dispatcher redirect. That is, the request is kept inside
     * the servlet container. This has nothing to do with HTTP 3xx status messages.
     * <p>
     * Filters can act upon this to not perform their logic in this case.
     */
    public static boolean isInternalRedirect(final HttpServletRequest request) {
        final Boolean isRedirect = (Boolean) request.getAttribute(REDIRECT_ATTRIBUTE);
        if (isRedirect != null) {
            return isRedirect;
        }
        return false;
    }

    /**
     * Mark the request as having a response with a redirect.
     */
    public static void markAsInternalRedirect(final HttpServletRequest request) {
        request.setAttribute(REDIRECT_ATTRIBUTE, true);
    }

    /**
     * Remove the mark (if any) that the request is a redirect.
     */
    public static void unmarkAsInternalRedirect(final HttpServletRequest request) {
        request.setAttribute(REDIRECT_ATTRIBUTE, false);
    }
}
