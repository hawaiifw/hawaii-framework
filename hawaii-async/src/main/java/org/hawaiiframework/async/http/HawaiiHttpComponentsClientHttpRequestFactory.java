/*
 * Copyright 2015-2017 the original author or authors.
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
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;
import org.hawaiiframework.async.timeout.TaskAbortStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * An HTTP request factory that sets the {@link TaskAbortStrategy} for the request.
 * <p>
 * It extends the (default) {@link HttpComponentsClientHttpRequestFactory} and uses the postProcessHttpRequest method to register
 * the {@link TaskAbortStrategy}.
 *
 * @since 2.0.0
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 */
public class HawaiiHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    /**
     * {@inheritDoc}
     * <p>
     * Register the abort strategy for this request.
     */
    @Override
    protected void postProcessHttpRequest(@NotNull final HttpUriRequest request) {
        requireNonNull(request);
        super.postProcessHttpRequest(request);
        SharedTaskContextHolder.setTaskAbortStrategy(new HttpComponentHttpRequestTaskAbortStrategy(request));
    }

}
