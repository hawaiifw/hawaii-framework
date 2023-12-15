package org.hawaiiframework.logging.web.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility for Servlet Filters.
 */
public final class ServletFilterUtil {

    /**
     * The request attribute to set when a response is a redirect.
     */
    private static final String REDIRECT_ATTRIBUTE = "is_internal_redirect";

    /**
     * The request attribute to set when a response is handled asynchronously.
     */
    private static final String ASYNC_ATTRIBUTE = "is_internal_async";

    /**
     * The request attribute to set when a request is logged.
     */
    private static final String REQUEST_IS_LOGGED_ATTRIBUTE = "request_is_logged";

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
     *
     * @param request The request to check.
     * @return Whether the request is an internal redirect.
     */
    public static boolean isOriginalRequest(final HttpServletRequest request) {
        final Boolean isRedirect = get(request, REDIRECT_ATTRIBUTE);

        final Boolean isASync = get(request, ASYNC_ATTRIBUTE);

        return !(isRedirect || isASync);
    }

    private static Boolean get(final HttpServletRequest request, final String redirectAttribute) {
        Boolean attribute = (Boolean) request.getAttribute(redirectAttribute);
        if (attribute == null) {
            attribute = false;
        }
        return attribute;
    }

    /**
     * Mark the request as having a response with a redirect.
     *
     * @param request The request to mark as internal redirect.
     */
    public static void markAsInternalRedirect(final HttpServletRequest request) {
        request.setAttribute(REDIRECT_ATTRIBUTE, true);
    }

    /**
     * Remove the mark (if any) that the request is a redirect.
     *
     * @param request The request to un-mark as redirect.
     */
    public static void unmarkAsInternalRedirect(final HttpServletRequest request) {
        request.setAttribute(REDIRECT_ATTRIBUTE, false);
    }

    /**
     * Mark the request as having a response handled asynchronously.
     *
     * @param request The request to mark as handled asynchronously.
     */
    public static void markAsAsyncHandling(final HttpServletRequest request) {
        request.setAttribute(ASYNC_ATTRIBUTE, true);
    }

    /**
     * Remove the mark (if any) that the request is handled asynchronously.
     *
     * @param request The request to un-mark as handled asynchronously.
     */
    public static void unmarkAsAsyncHandling(final HttpServletRequest request) {
        request.setAttribute(ASYNC_ATTRIBUTE, false);
    }

    /**
     * Mark the request as logged.
     *
     * @param request The request to mark as logged.
     */
    public static void markLogged(final HttpServletRequest request) {
        request.setAttribute(REQUEST_IS_LOGGED_ATTRIBUTE, true);
    }

    /**
     * Has the request been logged (marked as such).
     *
     * @param request The request the check.
     * @return {@code true} if the {@link #markLogged(HttpServletRequest)} has been called.
     */
    public static boolean isLogged(final HttpServletRequest request) {
        return get(request, REQUEST_IS_LOGGED_ATTRIBUTE);
    }
}
