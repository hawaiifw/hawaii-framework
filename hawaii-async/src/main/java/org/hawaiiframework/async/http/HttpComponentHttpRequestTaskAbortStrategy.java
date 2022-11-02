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

package org.hawaiiframework.async.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Strategy to abort and Http Components HTTP request.
 * <p>
 * These requests are used by (for instance) Spring's RestTemplate.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class HttpComponentHttpRequestTaskAbortStrategy implements TaskAbortStrategy {

    /**
     * The logger to use.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpComponentHttpRequestTaskAbortStrategy.class);

    /**
     * The request we may have to abort.
     */
    private final HttpUriRequest request;

    /**
     * Construct a new instance with the {@code request} we may have to abort.
     *
     * @param request The request about to be executed (which we have to guard).
     */
    public HttpComponentHttpRequestTaskAbortStrategy(@NotNull final HttpUriRequest request) {
        this.request = requireNonNull(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean invoke() {
        try {
            LOGGER.trace("Invoking HttpUriRequest#abort().");
            request.abort();
            return true;
        } catch (UnsupportedOperationException e) {
            LOGGER.error("Cannot stop http request.", e);
            return false;
        }
    }
}
