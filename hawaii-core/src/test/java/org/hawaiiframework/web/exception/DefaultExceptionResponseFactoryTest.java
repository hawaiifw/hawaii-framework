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

import org.hawaiiframework.exception.TestApiException;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ApiErrorResponseResource;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResponseResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultExceptionResponseFactoryTest {

    private DefaultExceptionResponseFactory exceptionResponseFactory;

    @Before
    public void setup() throws Exception {
        exceptionResponseFactory = new DefaultExceptionResponseFactory();
    }

    @Test
    public void thatNullExceptionReturnsDefaultErrorResource() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(null);
        assertTrue("Not a ErrorResponseResource", resource != null);
    }

    @Test
    public void thatApiResourceIsCreatedForApiException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new TestApiException());
        assertTrue("Not a ApiErrorResponseResource", resource instanceof ApiErrorResponseResource);
    }

    @Test
    public void thatValidationErrorResourceIsCreatedForValidationException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new ValidationException());
        assertTrue("Not a ValidationErrorResponseResource", resource instanceof ValidationErrorResponseResource);
    }

    @Test
    public void thatApiResourceIsCreatedForApiExceptionCausedByAnotherHawaiiException() throws Exception {
        ErrorResponseResource resource = exceptionResponseFactory.create(new TestApiException(new ValidationException()));
        assertTrue("Not a ApiErrorResponseResource", resource instanceof ApiErrorResponseResource);
    }

    @Test
    public void thatFirstLevelHawaiiExceptionIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        ErrorResponseResource errorResponseResource = exceptionResponseFactory.create(cause);
        assertEquals("Wrong exception", cause, errorResponseResource.getThrowable());
    }

    @Test
    public void thatFirstLevelOtherExceptionIsReturned() throws Exception {
        Throwable cause = new IllegalArgumentException();
        ErrorResponseResource errorResponseResource = exceptionResponseFactory.create(cause);
        assertEquals("Wrong exception", cause, errorResponseResource.getThrowable());
    }

    @Test
    public void thatContainedApiExceptionIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        ErrorResponseResource apiErrorResource = exceptionResponseFactory.create(cause);
        Throwable throwable = apiErrorResource.getThrowable();
        Throwable result = exceptionResponseFactory.create(throwable).getThrowable();
        assertEquals("Wrong exception", cause, result);
    }

    @Test
    public void thatContainedApiExceptionInTwoLevelsIsReturned() throws Exception {
        Throwable cause = new TestApiException();
        Throwable throwable = new IllegalStateException(cause);
        Throwable result = exceptionResponseFactory.create(throwable).getThrowable();
        assertEquals("Wrong exception", cause, result);
    }

}
