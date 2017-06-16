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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This enricher copies the http status value and text onto the error response resource.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public class ErrorResponseStatusEnricher implements ErrorResponseEnricher {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doEnrich(
            final ErrorResponseResource errorResponseResource,
            final Throwable throwable,
            final WebRequest request,
            final HttpStatus httpStatus) {
        errorResponseResource.setStatusCode(httpStatus.value());
        errorResponseResource.setStatusMessage(httpStatus.getReasonPhrase());
    }

}
