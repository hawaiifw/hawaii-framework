package org.hawaiiframework.web.exception;

import org.hawaiiframework.exception.TestApiException;
import org.hawaiiframework.validation.ValidationException;
import org.hawaiiframework.web.resource.ApiErrorResponseResource;
import org.hawaiiframework.web.resource.ErrorResponseResource;
import org.hawaiiframework.web.resource.ValidationErrorResponseResource;
import org.junit.Before;
import org.junit.Test;

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

}
