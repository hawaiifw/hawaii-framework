/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawaiiframework.logging.web.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUpgradeHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HttpServletRequestWrapper that allows resetting of the input stream.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ResettableHttpServletRequest extends HttpServletRequestWrapper {

    /**
     * The original request.
     */
    private final HttpServletRequest request;

    /**
     * The (wrapped) response.
     */
    private final HttpServletResponse response;

    /**
     * The input stream we can reset.
     */
    private ResettableServletInputStream servletStream;

    /**
     * The constructor.
     */
    public ResettableHttpServletRequest(final HttpServletRequest request, final HttpServletResponse response) {
        super(request);
        this.request = request;
        this.response = response;
    }

    /**
     * Reset the input stream so we can read it again.
     */
    public void reset() throws IOException {
        if (this.servletStream != null) {
            this.servletStream.reset();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
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
        return new BufferedReader(new InputStreamReader(getInputStream(), request.getCharacterEncoding()));
    }

    private byte[] copyRawData() throws IOException {
        return IOUtils.toByteArray(request.getReader(), request.getCharacterEncoding());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_SWITCHING_PROTOCOLS);
        return request.upgrade(httpUpgradeHandlerClass);
    }
}
