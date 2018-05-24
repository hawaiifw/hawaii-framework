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

package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.ApiException;
import org.hawaiiframework.web.resource.ApiErrorResponseResource;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This enricher adds api error information to the error response resource.
 *
 * It only applies to an {@link ApiException}.
 *
 * @author Paul Klos
 * @since 2.0.0
 */
public class ApiErrorResponseEnricher implements ErrorResponseEnricher {

    /**
     * {@inheritDoc}
     *
     * <p><strong>NOTE:</strong> This enricher only applies if throwable is an {@link ApiException} and
     * #errorResponseResource is an {@link ApiErrorResponseResource}.</p>
     */
    @Override
    public void doEnrich(
            final ErrorResponseResource errorResponseResource,
            final Throwable throwable,
            final WebRequest request,
            final HttpStatus httpStatus) {
        if (throwable instanceof ApiException && errorResponseResource instanceof ApiErrorResponseResource) {
            final ApiException apiException = (ApiException) throwable;
            final ApiErrorResponseResource responseResource = (ApiErrorResponseResource) errorResponseResource;
            responseResource.setApiErrorCode(apiException.getErrorCode());
            responseResource.setApiErrorReason(apiException.getReason());
        }

    }
}
