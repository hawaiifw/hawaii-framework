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

import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An extension of {@link ContentCachingResponseWrapper} that keeps track whether the response is a redirect.
 *
 * @author Rutger Lubbers
 * @since 2.0.0
 */
public class ContentCachingWrappedResponse extends ContentCachingResponseWrapper {

    /**
     * Flag to indicate that the response is a redirect.
     */
    private boolean redirect;

    /**
     * The constructor.
     */
    public ContentCachingWrappedResponse(final HttpServletResponse response) {
        super(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(final int statusCode) throws IOException {
        redirect = true;
        super.sendError(statusCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(final int statusCode, final String message) throws IOException {
        redirect = true;
        super.sendError(statusCode, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(final String location) throws IOException {
        redirect = true;
        super.sendRedirect(location);
    }

    @SuppressWarnings("PMD.CommentRequired")
    public boolean isRedirect() {
        return redirect;
    }
}
