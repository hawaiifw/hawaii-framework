package org.hawaiiframework.logging.web.filter;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HttpServletRequestWrapper that allows resetting of the input stream.
 */
public class ResettableHttpServletRequest extends HttpServletRequestWrapper {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResettableHttpServletRequest.class);

    /**
     * The original request.
     */
    private final HttpServletRequest request;

    /**
     * The input stream we can reset.
     */
    private ResettableServletInputStream servletStream;

    /**
     * The default constructor.
     * <p>
     * Note, there is a {@code getParameterNames()} call. We do this call to force reading the POST parameters.
     * This should not be needed, normally.
     * <p>
     * However, if we log the HTTP request body, then subsequent calls to getParameter(...) will return null.
     * Even if the POST contains the parameters.
     * <p>
     * It seems that some servlet containers lazily load the parameters and detect whether the input stream
     * (or input body) is already read. In that case, the call to read a parameter will return null.
     * <p>
     * Thus, without reading the parameter names (which forces parsing of the request body), the application fails.
     * With the {@code getParameterNames()} call we can log the request body _and_ allow the application to access
     * the POST parameters.
     *
     * In other words, this is a sad but necessary workaround.
     */
    @SuppressWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public ResettableHttpServletRequest(final HttpServletRequest request) {
        super(request);
        this.request = request;
        getParameterNames();
    }

    /**
     * Reset the input stream so we can read it again.
     */
    public void reset() throws IOException {
        this.servletStream.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        LOGGER.debug("getInputStream() invoked");
        if (servletStream == null) {
            servletStream = new ResettableServletInputStream(copyRawData());
        }
        return servletStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedReader getReader() throws IOException {
        LOGGER.debug("getReader() invoked");
        return new BufferedReader(new InputStreamReader(getInputStream(), request.getCharacterEncoding()));
    }

    private byte[] copyRawData() throws IOException {
        return IOUtils.toByteArray(request.getReader(), request.getCharacterEncoding());
    }

}
